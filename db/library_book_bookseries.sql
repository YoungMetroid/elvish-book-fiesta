select b.title || ' ' || b.volume as 'name'
,b.author
,bs.total_volumes
 from book_series bs 
join book b on b.series_id = bs.id




