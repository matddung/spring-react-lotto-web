import { useMemo } from 'react';

const usePagination = (totalItems, itemsPerPage, currentPage, setCurrentPage) => {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    const displayedPageNumbers = useMemo(() => {
        const pages = [];
        for (let i = 1; i <= totalPages; i++) {
            pages.push(i);
        }
        const chunkSize = 10;
        const chunkIndex = Math.floor((currentPage - 1) / chunkSize);
        return pages.slice(chunkIndex * chunkSize, chunkIndex * chunkSize + chunkSize);
    }, [totalPages, currentPage]);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handlePreviousPages = () => {
        setCurrentPage(prevPage => Math.max(prevPage - 10, 1));
    };

    const handleNextPages = () => {
        setCurrentPage(prevPage => Math.min(prevPage + 10, totalPages));
    };

    const handleFirstPage = () => {
        setCurrentPage(1);
    };

    const handleLastPage = () => {
        setCurrentPage(totalPages);
    };

    return {
        startIndex,
        endIndex,
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