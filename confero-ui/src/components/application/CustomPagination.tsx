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
    currentPage?: number
    totalPages?: number
    onPageChange?: (page: number) => void
}

export default function CustomPagination({
                                      currentPage = 1,
                                      totalPages = 1,
                                      onPageChange = () => {},
                                  }: CustomPaginationProps) {
    const getPageNumbers = () => {
        const pages = []
        const maxVisiblePages = 3

        if (totalPages <= maxVisiblePages + 2) {
            for (let i = 1; i <= totalPages; i++) {
                pages.push(i)
            }
        } else {
            pages.push(1)

            if (currentPage <= maxVisiblePages) {
                for (let i = 2; i <= maxVisiblePages; i++) {
                    pages.push(i)
                }
                pages.push("...")
                pages.push(totalPages)
            } else if (currentPage > totalPages - maxVisiblePages) {
                pages.push("...")
                for (let i = totalPages - maxVisiblePages + 1; i <= totalPages; i++) {
                    pages.push(i)
                }
            } else {
                pages.push("...")
                pages.push(currentPage - 1)
                pages.push(currentPage)
                pages.push(currentPage + 1)
                pages.push("...")
                pages.push(totalPages)
            }
        }

        return pages
    }

    const pages = getPageNumbers()

    return (
        <div className={"w-full justify-center bg-white"}>
        <Pagination className={"fixed bottom-12 pb-3 mb-4 left-0 right-0 bg-white"}>
            <PaginationContent>
                <PaginationItem>
                    <PaginationPrevious
                        href="#"
                        onClick={(e) => {
                            e.preventDefault()
                            if (currentPage > 1) {
                                onPageChange(currentPage - 1)
                            }
                        }}
                        aria-disabled={currentPage === 1}
                        className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
                    />
                </PaginationItem>

                {pages.map((page, index) => (
                    <PaginationItem key={index}>
                        {page === "..." ? (
                            <PaginationEllipsis  className={"cursor-pointer select-none"} />
                        ) : (
                            <PaginationLink
                                className={"cursor-pointer select-none"}
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault()
                                    onPageChange(Number(page))
                                }}
                                isActive={currentPage === page}
                            >
                                {page}
                            </PaginationLink>
                        )}
                    </PaginationItem>
                ))}

                <PaginationItem>
                    <PaginationNext
                        href="#"
                        onClick={(e) => {
                            e.preventDefault()
                            if (currentPage < totalPages) {
                                onPageChange(currentPage + 1)
                            }
                        }}
                        aria-disabled={currentPage === totalPages}
                        className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
                    />
                </PaginationItem>
            </PaginationContent>
        </Pagination>
        </div>
    )
}