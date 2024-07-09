import { useMemo } from 'react';

const usePagination = (totalItems, itemsPerPage, currentPage, setCurrentPage, chunkSize = 10) => {
    const totalPages = Math.ceil(totalItems / itemsPerPage);

    const displayedPageNumbers = useMemo(() => {
        const pages = [];
        for (let i = 1; i <= totalPages; i++) {
            pages.push(i);
        }
        const chunkIndex = Math.floor((currentPage - 1) / chunkSize);
        return pages.slice(chunkIndex * chunkSize, chunkIndex * chunkSize + chunkSize);
    }, [totalPages, currentPage, chunkSize]);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handlePreviousPages = () => {
        setCurrentPage(prevPage => Math.max(prevPage - chunkSize, 1));
    };

    const handleNextPages = () => {
        setCurrentPage(prevPage => Math.min(prevPage + chunkSize, totalPages));
    };

    const handleFirstPage = () => {
        setCurrentPage(1);
    };

    const handleLastPage = () => {
        setCurrentPage(totalPages);
    };

    return {
        displayedPageNumbers,
        totalPages,
        handlePageChange,
        handlePreviousPages,
        handleNextPages,
        handleFirstPage,
        handleLastPage
    };
};

export default usePagination;