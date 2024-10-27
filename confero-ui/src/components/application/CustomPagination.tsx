import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination"

interface CustomPaginationProps {
    currentPage: number
    totalPages: number
    onPageChange: (page: number) => void
}

const CustomPagination =({
                                      currentPage = 3,
                                      totalPages = 10,
                                      onPageChange = () => {},
                                  }: CustomPaginationProps) => {
    const renderPageNumbers = () => {
        const pageNumbers = []
        const maxVisiblePages = 3

        for (let i = 1; i <= Math.min(maxVisiblePages, totalPages); i++) {
            pageNumbers.push(
                <PaginationItem key={i}>
                    <PaginationLink
                        onClick={() => onPageChange(i)}
                        isActive={currentPage === i}
                    >
                        {i}
                    </PaginationLink>
                </PaginationItem>
            )
        }

        if (totalPages > maxVisiblePages) {
            pageNumbers.push(
                <PaginationItem key="ellipsis">
                    <PaginationEllipsis />
                </PaginationItem>
            )
        }

        return pageNumbers
    }

    return (
        <div className={"w-full justify-center bg-white"}>
            <Pagination className={"fixed bottom-12 pb-3 mb-4 left-0 right-0 bg-white"}>
                <PaginationContent>
                    <PaginationItem>
                        <PaginationPrevious
                            onClick={() => onPageChange(Math.max(1, currentPage - 1))}
                        />
                    </PaginationItem>
                    {renderPageNumbers()}
                    <PaginationItem>
                        <PaginationNext
                            onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
                        />
                    </PaginationItem>
                </PaginationContent>
            </Pagination>
        </div>

    )
}

export default CustomPagination;