const express = require('express');
const axios = require('axios');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const GTFS_DATA_PATH = process.env.GTFS_DATA_PATH || path.resolve(__dirname, 'gtfs_transit_data.json');
const OTP_BASE_URL = (process.env.OTP_URL || 'http://localhost:8080/otp/routers/default').replace(/\/plan\/?$/, '');

const app = express();
app.use(cors());
app.use(express.json());

app.get('/api/v1/transit/data', (req, res) => {
    try {
        if (!fs.existsSync(GTFS_DATA_PATH)) {
            return res.status(404).json({
                error: 'Transit data not found. Set GTFS_DATA_PATH or mount gtfs_transit_data.json.'
            });
        }
        res.json(JSON.parse(fs.readFileSync(GTFS_DATA_PATH, 'utf8')));
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// --- STREET PATH GENERATOR ---
function generateStreetPath(lat1, lon1, lat2, lon2, segmentsCount = 6, offsetMagnitude = 0.00035) {
    let pts = [[lon1, lat1]];
    for (let i = 1; i < segmentsCount; i++) {
        const t = i / segmentsCount;
        let lat = lat1 + (lat2 - lat1) * t;
        let lon = lon1 + (lon2 - lon1) * t;

        const perpLat = -(lon2 - lon1);
        const perpLon = (lat2 - lat1);
        const len = Math.sqrt(perpLat * perpLat + perpLon * perpLon);
        if (len > 0) {
            const side = (i % 2 === 0 ? 1 : -1) * offsetMagnitude * (1.0 - Math.abs(t - 0.5) * 0.5);
            lat += (perpLat / len) * side;
            lon += (perpLon / len) * side;
        }
        pts.push([lon, lat]);
    }
    pts.push([lon2, lat2]);
    return pts;
}

// --- MOCK ROUTE GENERATOR (used when OTP has no active service) ---
function generateMockRoute(fromLat, fromLon, toLat, toLon, mode) {
    fromLat = parseFloat(fromLat);
    fromLon = parseFloat(fromLon);
    toLat = parseFloat(toLat);
    toLon = parseFloat(toLon);

    const R = 6371e3;
    const phi1 = fromLat * Math.PI / 180;
    const phi2 = toLat * Math.PI / 180;
    const deltaPhi = (toLat - fromLat) * Math.PI / 180;
    const deltaLambda = (toLon - fromLon) * Math.PI / 180;
    const a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
        Math.cos(phi1) * Math.cos(phi2) *
        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distanceMeters = R * c;

    let speed = 1.4;
    let totalDurationSeconds = distanceMeters / speed;
    let features = [];
    let now = Date.now();

    if (distanceMeters < 350) {
        features.push({
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates: generateStreetPath(fromLat, fromLon, toLat, toLon, 4, 0.00008)
            },
            properties: {
                mode: 'WALK',
                duration: Math.max(1, Math.round(distanceMeters / (1.4 * 60))),
                distance: distanceMeters,
                instructions: 'Mergi pe jos către destinație',
                color: '#4285F4',
                startTime: now
            }
        });
        totalDurationSeconds = distanceMeters / 1.4;
    } else if (mode.includes('CAR')) {
        speed = 10.0;
        totalDurationSeconds = distanceMeters / speed;
        features.push({
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates: generateStreetPath(fromLat, fromLon, toLat, toLon, 10, 0.00045)
            },
            properties: {
                mode: 'CAR',
                duration: Math.max(2, Math.round(totalDurationSeconds / 60)),
                distance: distanceMeters,
                instructions: 'Condu spre destinație',
                color: '#747d8c',
                startTime: now
            }
        });
    } else if (mode.includes('WALK') && !mode.includes('TRANSIT')) {
        features.push({
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates: generateStreetPath(fromLat, fromLon, toLat, toLon, 8, 0.0002)
            },
            properties: {
                mode: 'WALK',
                duration: Math.max(3, Math.round(totalDurationSeconds / 60)),
                distance: distanceMeters,
                instructions: 'Mergi pe jos către destinație',
                color: '#4285F4',
                startTime: now
            }
        });
    } else {
        const walk1Dist = distanceMeters * 0.15;
        const transitDist = distanceMeters * 0.70;
        const walk2Dist = distanceMeters * 0.15;
        const walkSpeed = 1.4;
        const transitSpeed = 8.0;
        const dur1 = walk1Dist / walkSpeed;
        const dur2 = transitDist / transitSpeed;
        const dur3 = walk2Dist / walkSpeed;
        totalDurationSeconds = dur1 + dur2 + dur3;

        const startStopLat = fromLat + (toLat - fromLat) * 0.15 + (toLon - fromLon) * 0.02;
        const startStopLon = fromLon + (toLon - fromLon) * 0.15 - (toLat - fromLat) * 0.02;
        const endStopLat = fromLat + (toLat - fromLat) * 0.85 - (toLon - fromLon) * 0.02;
        const endStopLon = fromLon + (toLon - fromLon) * 0.85 + (toLat - fromLat) * 0.02;

        const busLines = [
            { name: '4', color: '#e74c3c' },
            { name: '20', color: '#2ecc71' },
            { name: '17', color: '#3498db' },
            { name: '34', color: '#9b59b6' },
            { name: '50', color: '#e67e22' }
        ];
        const line = busLines[Math.floor(Math.abs(toLat + toLon) * 100) % busLines.length];

        features.push({
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates: generateStreetPath(fromLat, fromLon, startStopLat, startStopLon, 5, 0.00015)
            },
            properties: {
                mode: 'WALK',
                duration: Math.max(1, Math.round(dur1 / 60)),
                distance: walk1Dist,
                instructions: 'Walk din start spre Stația de autobuz',
                color: '#4285F4',
                startTime: now
            }
        });
        features.push({
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates: generateStreetPath(startStopLat, startStopLon, endStopLat, endStopLon, 12, 0.00045)
            },
            properties: {
                mode: 'TRANSIT',
                duration: Math.max(2, Math.round(dur2 / 60)),
                distance: transitDist,
                instructions: `Bus ${line.name} din Stația Centrală`,
                color: line.color,
                startTime: now + dur1 * 1000
            }
        });
        features.push({
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates: generateStreetPath(endStopLat, endStopLon, toLat, toLon, 5, 0.00015)
            },
            properties: {
                mode: 'WALK',
                duration: Math.max(1, Math.round(dur3 / 60)),
                distance: walk2Dist,
                instructions: 'Walk spre destinație',
                color: '#4285F4',
                startTime: now + (dur1 + dur2) * 1000
            }
        });
    }

    return {
        type: 'FeatureCollection',
        metadata: {
            totalDurationMinutes: Math.max(1, Math.round(totalDurationSeconds / 60)),
            transfers: mode.includes('TRANSIT') && distanceMeters >= 350 ? 1 : 0,
            distance: Math.round(distanceMeters),
            startTime: now,
            endTime: now + totalDurationSeconds * 1000
        },
        features
    };
}

app.post('/api/v1/routing/plan', async (req, res) => {
    const { fromLat, fromLon, toLat, toLon, mode } = req.body;
    const otpMode = mode || 'WALK,TRANSIT';

    try {
        console.log(`[Proxy] Routing (${otpMode}): ${fromLat},${fromLon} -> ${toLat},${toLon}`);

        const now = new Date();
        const dateStr = now.toISOString().split('T')[0];
        const timeStr = `${now.getHours()}:${now.getMinutes()}`;
        const otpUrl = `${OTP_BASE_URL}/plan`;

        let response;
        try {
            response = await axios.get(otpUrl, {
                params: {
                    fromPlace: `${fromLat},${fromLon}`,
                    toPlace: `${toLat},${toLon}`,
                    mode: otpMode,
                    date: dateStr,
                    time: timeStr,
                    numItineraries: 10,
                    maxWalkDistance: 2000
                },
                timeout: 3000
            });
        } catch (axiosErr) {
            console.warn(`[Proxy] OTP unreachable. Mock routing. Reason: ${axiosErr.message}`);
            return res.json(generateMockRoute(fromLat, fromLon, toLat, toLon, otpMode));
        }

        let routeData = response.data;

        if (!routeData.plan?.itineraries?.length) {
            console.log('[Proxy] No routes now. Trying tomorrow 07:00...');
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            try {
                const fallbackResponse = await axios.get(otpUrl, {
                    params: {
                        fromPlace: `${fromLat},${fromLon}`,
                        toPlace: `${toLat},${toLon}`,
                        mode: otpMode,
                        date: tomorrow.toISOString().split('T')[0],
                        time: '07:00',
                        numItineraries: 10,
                        maxWalkDistance: 2000
                    },
                    timeout: 3000
                });
                if (fallbackResponse.data.plan?.itineraries?.length) {
                    routeData = fallbackResponse.data;
                } else {
                    console.warn('[Proxy] No tomorrow itineraries. Mock routing.');
                    return res.json(generateMockRoute(fromLat, fromLon, toLat, toLon, otpMode));
                }
            } catch (err) {
                console.warn(`[Proxy] Fallback failed: ${err.message}. Mock routing.`);
                return res.json(generateMockRoute(fromLat, fromLon, toLat, toLon, otpMode));
            }
        }

        const transitItineraries = routeData.plan.itineraries.filter(it =>
            it.legs.some(leg => leg.mode !== 'WALK')
        );
        const itinerary = transitItineraries.length > 0
            ? transitItineraries.sort((a, b) => a.legs.length - b.legs.length)[0]
            : routeData.plan.itineraries[0];

        const features = itinerary.legs.map(leg => ({
            type: 'Feature',
            geometry: {
                type: 'LineString',
                coordinates: decodePolyline(leg.legGeometry.points)
            },
            properties: {
                mode: leg.mode,
                duration: Math.round(leg.duration / 60),
                distance: leg.distance,
                instructions: leg.mode === 'WALK' ? 'Walk' : (leg.mode === 'CAR' ? 'Drive' : `Bus ${leg.routeShortName} din ${leg.from.name}`),
                color: leg.mode === 'WALK' ? '#4285F4' : (leg.mode === 'CAR' ? '#747d8c' : (leg.routeColor ? `#${leg.routeColor}` : '#1a1a1a')),
                startTime: leg.startTime
            }
        }));

        res.json({
            type: 'FeatureCollection',
            metadata: {
                totalDurationMinutes: Math.round(itinerary.duration / 60),
                transfers: itinerary.transfers,
                distance: itinerary.legs.reduce((acc, leg) => acc + leg.distance, 0),
                startTime: itinerary.startTime,
                endTime: itinerary.endTime
            },
            features
        });
    } catch (err) {
        console.error('[Proxy] Routing error:', err.message);
        res.json(generateMockRoute(fromLat, fromLon, toLat, toLon, otpMode));
    }
});

function decodePolyline(str) {
    let index = 0, lat = 0, lng = 0, coordinates = [];
    while (index < str.length) {
        let b, shift = 0, result = 0;
        do {
            b = str.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        lat += ((result & 1) ? ~(result >> 1) : (result >> 1));
        shift = 0;
        result = 0;
        do {
            b = str.charCodeAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        lng += ((result & 1) ? ~(result >> 1) : (result >> 1));
        coordinates.push([lng / 100000, lat / 100000]);
    }
    return coordinates;
}

app.listen(8081, () => console.log('🚀 PWA Bridge running on http://localhost:8081'));
