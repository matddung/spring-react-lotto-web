import React, { useEffect, useState } from 'react';

import { generateRandomNumbers, getPatternRecognition, getEnsemblePrediction, runMonteCarloSimulation, getStatisticsTop6, getCurrentUserLottoInfo } from '../../util/WekaAPIUtil';
import './WekaPage.css';

const WekaPage = () => {
    const [numbers, setNumbers] = useState({
        random: [],
        pattern: [],
        ensemble: [],
        monteCarlo: [],
        top6: []
    });
    const [visible, setVisible] = useState({
        random: false,
        pattern: false,
        ensemble: false,
        monteCarlo: false,
        top6: false
    });

    const [alreadyReceived, setAlreadyReceived] = useState(false);

    const getClosestSaturday = (date) => {
        const day = date.getDay();
        const diff = 6 - day;
        const closestSaturday = new Date(date);
        closestSaturday.setDate(date.getDate() + diff);
        return closestSaturday;
    };

    const formatDate = (date) => {
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-indexed
        const dd = String(date.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    };

    useEffect(() => {
        const fetchData = async () => {
            const today = new Date();
            const closestSaturday = getClosestSaturday(today);
            const formattedDate = formatDate(closestSaturday);

            const random = await generateRandomNumbers();
            const pattern = await getPatternRecognition(formattedDate);
            const ensemble = await getEnsemblePrediction(formattedDate);
            const monteCarlo = await runMonteCarloSimulation();
            const top6 = await getStatisticsTop6();

            setNumbers({
                random: Array.isArray(random) ? random : [],
                pattern: Array.isArray(pattern) ? pattern : [],
                ensemble: Array.isArray(ensemble) ? ensemble : [],
                monteCarlo: Array.isArray(monteCarlo) ? monteCarlo : [],
                top6: Array.isArray(top6) ? top6 : []
            });

            if (null) {
                setAlreadyReceived(true);
            }
        };
        fetchData();
    }, []);

    const handleBoxClick = (key) => {
        if (alreadyReceived) {
            return;
        }

        setVisible((prevVisible) => ({
            ...prevVisible,
            [key]: !prevVisible[key]
        }));
    };

    const containers = [
        { title: 'Random Numbers', data: numbers.random, key: 'random' },
        { title: 'Pattern Recognition', data: numbers.pattern, key: 'pattern' },
        { title: 'Ensemble Prediction', data: numbers.ensemble, key: 'ensemble' },
        { title: 'Monte Carlo Simulation', data: numbers.monteCarlo, key: 'monteCarlo' },
        { title: 'Statistics Top 6', data: numbers.top6, key: 'top6' }
    ];

    return (
        <div className="weka-page-container">
            <h2 className='h2-top-text'>추천</h2>
            <p className='info-text'>번호는 1주일에 한번만 받을 수 있습니다. 이미 받아보셨으면 다음 주를 기다려주세요.</p>
            <div className="weka-page-container-case">
                {containers.map((container, index) => (
                    <div key={index} className={`case-index ${alreadyReceived ? 'disabled' : ''}`} onClick={() => handleBoxClick(container.key)}>
                        <h3>{container.title}</h3>
                        {visible[container.key] && (
                            <div className="number-box-wrapper">
                                {container.data.slice(0, 6).map((number, idx) => (
                                    <div key={idx} className="number-box">
                                        {number}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                ))}
            </div>
            {alreadyReceived && <p className='already-received-text'>이미 금주의 번호를 받아보셨습니다. 다음 주를 기다려주세요.</p>}
        </div>
    );
}

export default WekaPage;