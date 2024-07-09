import React, { useRef } from 'react';

import './Home.css';

const Home = () => {
    const modalRef1 = useRef();
    const modalRef2 = useRef();
    const modalRef3 = useRef();
    const modalRef4 = useRef();
    const modalRef5 = useRef();

    const openModal = (ref) => {
        ref.current.style.display = 'block';
    };

    const closeModal = (ref) => {
        ref.current.style.display = 'none';
    };

    const handleOutsideClick = (e, ref) => {
        if (e.target === ref.current) {
            closeModal(ref);
        }
    };

    return (
        <div className="home-container">
            <h1 className="home-title-h1">번호 추천 방법</h1>
            <h2 className="home-title-h2">번호는 Java Machine Learning Libray(Weka)를 이용해 만들어졌습니다. 재미로만 봐주세요</h2>
            <div className="methods-row">
            <div className="method-container" onClick={() => openModal(modalRef1)}>
                    <h2 className="method-title">방법 1<br/><br/>통계적 접근</h2>
                </div>
                <div className="method-container" onClick={() => openModal(modalRef2)}>
                    <h2 className="method-title">방법 2<br/><br/>패턴 인식</h2>
                </div>
                <div className="method-container" onClick={() => openModal(modalRef3)}>
                    <h2 className="method-title">방법 3<br/><br/>무작위 샘플링</h2>
                </div>
                <div className="method-container" onClick={() => openModal(modalRef4)}>
                    <h2 className="method-title">방법 4<br/><br/>합의 알고리즘</h2>
                </div>
                <div className="method-container" onClick={() => openModal(modalRef5)}>
                    <h2 className="method-title">방법 5<br/><br/>시뮬레이션</h2>
                </div>
            </div>
            <h2 className="home-title-h2">로그인, 회원가입을 제외한 모든 기능은 로그인 후 이용하실 수 있습니다.</h2>

            <div className="modal" ref={modalRef1} onClick={(e) => handleOutsideClick(e, modalRef1)}>
                <div className="modal-content">
                    <span className="close" onClick={() => closeModal(modalRef1)}>&times;</span>
                    <h2>통계적 접근</h2>
                    <p>과거의 로또 당첨 번호를 통계적으로 분석하여 특정 번호의 출현 확률을 계산합니다. 이를 통해 가장 자주 등장하는 번호를 찾아내고, 해당 번호들을 기반으로 새로운 조합을 생성합니다.</p>
                </div>
            </div>

            <div className="modal" ref={modalRef2} onClick={(e) => handleOutsideClick(e, modalRef2)}>
                <div className="modal-content">
                    <span className="close" onClick={() => closeModal(modalRef2)}>&times;</span>
                    <h2>패턴 인식</h2>
                    <p>과거의 로또 당첨 번호에서 패턴을 식별합니다. 이는 숫자 간의 관계나 특정 번호가 함께 출현하는 빈도 등을 분석하여 다음 당첨 번호를 예측하는 데 사용됩니다.</p>
                </div>
            </div>

            <div className="modal" ref={modalRef3} onClick={(e) => handleOutsideClick(e, modalRef3)}>
                <div className="modal-content">
                    <span className="close" onClick={() => closeModal(modalRef3)}>&times;</span>
                    <h2>무작위 샘플링</h2>
                    <p>컴퓨터 알고리즘을 사용하여 완전히 무작위로 숫자를 선택합니다. 이는 로또의 본질을 반영하는 방법으로, 복권 번호 생성기와 비슷한 방식입니다.</p>
                </div>
            </div>

            <div className="modal" ref={modalRef4} onClick={(e) => handleOutsideClick(e, modalRef4)}>
                <div className="modal-content">
                    <span className="close" onClick={() => closeModal(modalRef4)}>&times;</span>
                    <h2>합의 알고리즘</h2>
                    <p>여러 다른 번호 선택 방법(통계적 접근, 패턴 인식, 무작위 샘플링 등)을 결합하여 각 방법에서 선택된 번호들 간의 합의를 통해 최종 번호를 결정합니다.</p>
                </div>
            </div>

            <div className="modal" ref={modalRef5} onClick={(e) => handleOutsideClick(e, modalRef5)}>
                <div className="modal-content">
                    <span className="close" onClick={() => closeModal(modalRef5)}>&times;</span>
                    <h2>시뮬레이션</h2>
                    <p>컴퓨터 시뮬레이션(몬테카를로 시뮬레이션)을 사용하여 다양한 번호 조합을 생성하고, 이들 중에서 가장 가능성이 높은 조합을 선택합니다. 시뮬레이션은 10000번의 로또 추첨을 가정하여 실행됩니다.</p>
                </div>
            </div>
        </div>
    );
};

export default Home;