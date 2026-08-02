--Check that bookseries -> author_series -> authors
--are linked correctly
select bs.title
    ,bs.total_volumes
    ,bs.id
    ,a.id
    ,a.name
from book_series bs
join author_series atrs on bs.id = atrs.series_id
join author a on a.id = atrs.author_id;

