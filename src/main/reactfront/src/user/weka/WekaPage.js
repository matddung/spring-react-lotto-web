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

    useEffect(() => {
        const fetchData = async () => {
            const userInfo = await getCurrentUserLottoInfo();

            // 미리 userInfo에 있는 값을 numbers 상태로 설정
            setNumbers({
                random: userInfo.generateRandom ? userInfo.generateRandom.split(' ').map(Number) : [],
                pattern: userInfo.patternRecognition ? userInfo.patternRecognition.split(' ').map(Number) : [],
                ensemble: userInfo.ensembleLottoPrediction ? userInfo.ensembleLottoPrediction.split(' ').map(Number) : [],
                monteCarlo: userInfo.monteCarloSimulation ? userInfo.monteCarloSimulation.split(' ').map(Number) : [],
                top6: userInfo.top6Frequencies ? userInfo.top6Frequencies.split(' ').map(Number) : []
            });

            if (userInfo.alreadyReceived) {
                setAlreadyReceived(true);
            }
        };
        fetchData();
    }, []);

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

    const handleBoxClick = async (key) => {
        if (alreadyReceived) {
            return;
        }

        if (visible[key]) {
            setVisible((prevVisible) => ({
                ...prevVisible,
                [key]: false
            }));
            return;
        }

        let data;

        if (numbers[key].length > 0) {
            data = numbers[key];
        } else {
            const today = new Date();
            const closestSaturday = getClosestSaturday(today);
            const formattedDate = formatDate(closestSaturday);

            switch (key) {
                case 'random':
                    data = await generateRandomNumbers();
                    break;
                case 'pattern':
                    data = await getPatternRecognition(formattedDate);
                    break;
                case 'ensemble':
                    data = await getEnsemblePrediction(formattedDate);
                    break;
                case 'monteCarlo':
                    data = await runMonteCarloSimulation();
                    break;
                case 'top6':
                    data = await getStatisticsTop6();
                    break;
                default:
                    return;
            }

            if (!Array.isArray(data)) {
                data = [];
            }

            // Save the fetched data to numbers state
            setNumbers((prevNumbers) => ({
                ...prevNumbers,
                [key]: data
            }));
        }

        setVisible((prevVisible) => ({
            ...prevVisible,
            [key]: true
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
            <h1 className='h2-top-text'>번호 추천</h1><br/>
            <h3 className='h2-top-text'>이미 번호를 받아보셨다면, 다음 주를 기다려주세요. 다시 클릭하셔도 번호는 바뀌지 않습니다.</h3>
            <div className="weka-page-container-case">
                {containers.map((container, index) => (
                    <div key={index} className={`case-index ${alreadyReceived ? 'disabled' : ''}`} onClick={() => handleBoxClick(container.key)}>
                        <h3>{container.title}</h3>
                        {alreadyReceived ? (
                            <p className='info-text'>이미 번호를 받아보셨습니다. 다음 주를 기다려주세요.</p>
                        ) : (
                            <>
                                {numbers[container.key].length === 0 ? (
                                    <p className='info-text'>아직 번호를 받아보지 않으셨습니다.</p>
                                ) : (
                                    visible[container.key] && (
                                        <div className="number-box-wrapper">
                                            {container.data.slice(0, 6).map((number, idx) => (
                                                <div key={idx} className="number-box">
                                                    {number}
                                                </div>
                                            ))}
                                        </div>
                                    )
                                )}
                            </>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default WekaPage;