# API use

### All CRUD Operation

1. To get all posts: http://localhost:8080/api/posts
2. To get post by id : http://localhost:8080/api/posts/id id -> post id
3. To update post by id : http://localhost:8080/api/posts/id id -> post id with updated JSON object response
4. To delete post by id : http://localhost:8080/api/posts/id id -> post id

### Pagination

1. next page : http://localhost:8080/api/posts/next/page_number page_number -> 1,2,3..N
2. previous page : http://localhost:8080/api/posts/previous/page_number page_number -> N-1,...3,2,1

### Search

    search by content, title, author, tag, excerpt by api example:-> http://localhost:8080/api/posts/search/text 

1. text(by title like) :-> 'welcome to india'
2. text(by author like) :-> 'juli'
3. text(by content like) :-> 'country is so beautiful....'

### Sorting

1. Sort by published date in ascending order
    1. http://localhost:8080/api/posts/sort/asc
2. Sort by published date in descending order
    1. http://localhost:8080/api/posts/sort/desc

### Filter

1. filter by tags and author
    1. authorName = name and tagId=1 example:-> http://localhost:8080/api/posts/filter?authorName=bob&tagId=15&tagId=12
3. filter by date
    1. by date example:-> http://localhost:8080/api/posts/filter?dateStart=2023-08-27&dateEnd=2023-09-02
