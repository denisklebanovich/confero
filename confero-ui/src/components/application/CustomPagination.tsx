import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination.tsx";

const CustomPagination = ({
                              currentPage = 1,
                              totalPages = 2,
                              onPageChange = (number: number) => {},
                          }) => {
    const renderPageNumbers = () => {
        const pageNumbers = [];
        const maxVisiblePages = 3;

        if (totalPages <= maxVisiblePages) {
            for (let i = 1; i <= totalPages; i++) {
                pageNumbers.push(
                    <PaginationItem key={i}>
                        <PaginationLink
                            onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                            isActive={currentPage === i}
                        >
                            {i}
                        </PaginationLink>
                    </PaginationItem>
                );
            }
        } else {
            pageNumbers.push(
                <PaginationItem key={1}>
                    <PaginationLink
                        className={"select-none cursor-pointer"}
                        onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                        isActive={currentPage === 1}
                    >
                        {1}
                    </PaginationLink>
                </PaginationItem>
            );

            if (currentPage > 2) {
                pageNumbers.push(
                    <PaginationItem key="ellipsis-start" className={"select-none cursor-pointer"}>
                        <PaginationEllipsis />
                    </PaginationItem>
                );
            }

            const start = Math.max(2, currentPage - 1);
            const end = Math.min(totalPages - 1, currentPage + 1);

            for (let i = start; i <= end; i++) {
                if (i < totalPages) {
                    pageNumbers.push(
                        <PaginationItem key={i} className={"cursor-pointer select-none"}>
                            <PaginationLink
                                onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                                isActive={currentPage === i}
                            >
                                {i}
                            </PaginationLink>
                        </PaginationItem>
                    );
                }
            }
            if (currentPage < totalPages - 1) {
                pageNumbers.push(
                    <PaginationItem className={"cursor-pointer select-none"} key="ellipsis-end">
                        <PaginationEllipsis />
                    </PaginationItem>
                );
            }
            if (totalPages > 1) {
                pageNumbers.push(
                    <PaginationItem key={totalPages} className={"cursor-pointer select-none"}>
                        <PaginationLink
                            className={"select-none"}
                            onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                            isActive={currentPage === totalPages}
                        >
                            {totalPages}
                        </PaginationLink>
                    </PaginationItem>
                );
            }
        }

        return pageNumbers;
    };

    return (
        <div className={"w-full justify-center bg-white"}>
            <Pagination className={"fixed bottom-12 pb-3 mb-4 left-0 right-0 bg-white"}>
                <PaginationContent>
                    <PaginationItem>
                        <PaginationPrevious
                            className={"cursor-pointer select-none"}
                            onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                        />
                    </PaginationItem>

                    {renderPageNumbers()}
                    <PaginationItem>
                        <PaginationNext
                            className={"cursor-pointer select-none"}
                            onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                        />
                    </PaginationItem>
                </PaginationContent>
            </Pagination>
        </div>
    );
};

export default CustomPagination;
