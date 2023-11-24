# 📕 Booque ver.1
팀 프로젝트(6명) </br>
교보문고/YES24처럼 책 판매 사이트 & 리뷰 커뮤니티 활성화
# 사용기술
Java </br>
SQL </br>
Spring Boot </br>
HTML/CSS/Java Script
# 일정
2022/11/21 ~ 2022/12/23 </br>
# 특징
메인 홈(검색, 로그인, 카테고리, 마이페이지) </br>
블로그(포스트 글 CRUD, 댓글) </br>
책 상세 보기(찜, 하트) </br>
장바구니 </br>
결제
# 내가 맡은 기능
- 블로그(댓글 CRUD)
 #### Create
> postReply.js 일부
```java
    // 댓글 작성 함수
    function registerNewReply() {
        // 포스트 글
        const postId = document.querySelector('#postId').value;

        // 댓글 작성자
        const replyWriter = document.querySelector('#rWriter').value;
        console.log(replyWriter);
        if(replyWriter == "anonymousUser") {
            alert('로그인 후 이용 가능한 서비스입니다.');
            return;
        }
        // 댓글 내용
        const replyContent = document.querySelector('#replyContent').value;

        // 서버로 보낼 데이터
        const data = {
            postId: postId,
            replyContent: replyContent,
            replyWriter: replyWriter        
        };
        
        
        axios.post('/api/reply', data)
                .then(response => {
                    alert('#  댓글 등록 성공');
                    clearInputContent();
                    readAllReplies();
                    updateReplyCount();
                })
                .catch(error => {
                    console.log(error);
                });
    }
```
> PostReplyController.java 일부
```java
    // 댓글 작성
    @PostMapping
    public ResponseEntity<Integer> registerReply(@RequestBody ReplyRegisterDto dto) {
        log.info("registerReply()");

        Integer replyWriter = replyService.create(dto);

        return ResponseEntity.ok(replyWriter);
    }
```
 #### Read
> postReply.js 일부
```java
    // 댓글 목록 함수
    function readAllReplies(){
        const postId = document.querySelector('#postId').value;
        axios
        .get('/api/reply/all/' + postId)  
        .then(response => { updateReplyList(response.data) } )
        .catch(err => { console.log(err) });
    }
    function updateReplyList(data){
        const divReplies = document.querySelector('#replies');
        let str = '';
        for (let r of data){
            str += '<div class="card border-dark mb-3 w-100" style="text-align: left;">'
            + '<div class="flex-shrink-0"><img class="rounded-circle" width="60" height="60" src="' + r.userImage + '" alt="..." /></div>'

            + `<div class="fw-bold"><a href="/post/list?postWriter=${r.replyWriter}">${r.replyWriter}</a></div>`
                + '<div class="card-body text-dark">'
                + '<p class="card-text">' + r.replyContent + '</p>'
                + '<div><small style="color:gray;"> 작성시간: ' + '<span id="commentDate">' + r.createdTime + '</span>' + '</small></div>'
    //            + '<div><small style="color:gray;"> 수정시간: ' + r.modifiedTime + '</small></div>'
                + '</div>';
            if(r.replyWriter == loginUser){
            str += '<div class="card-footer">'
                + `<button type="button" class="btnModifies btn btn-outline-primary" data-rid="${r.replyId}">수정</button>`
                + '</div>';
            }
            str += '</div>';
        }
        
        divReplies.innerHTML = str;
}
```
> PostReplyController.java 일부
```java
    // 댓글 전체 리스트
    @GetMapping("/all/{postId}")
    public ResponseEntity<List<ReplyReadDto>> readAllReplies(@PathVariable Integer postId){
        log.info("readAllReplies(postId={})", postId);
        
        List<ReplyReadDto> list = replyService.readReplies(postId);
        
        log.info("# of list = {}", list.size());
       

        return ResponseEntity.ok(list);
    }
```
 #### Update/Delete
> postReply.js 일부
```java
    // 댓글 수정/삭제 모달 보여주는 함수
    function showReplyModal(reply) {
        modalReplyId.value = reply.replyId;
        modalReplyContent.value = reply.replyContent;
        postReplyModal.show();
    }
    // 몇 번 댓글을 수정할 것인지 정보 전달
    function getReply(event) {
        const rid = event.target.getAttribute('data-rid');
        axios
        .get('/api/reply/' + rid) 
        .then(response => { showReplyModal(response.data) })
        .catch(err => { console.log(err) });
    }
    // 댓글 삭제 함수
    function deleteReply(event) {
        const replyId = modalReplyId.value;
        const result = confirm('정말 삭제하시겠습니까?');
        if (result) {
            axios
            .delete('/api/reply/' + replyId) 
            .then(response => {
                alert(`# 댓글 삭제 성공`);
                readAllReplies();
                updateReplyCount();
             })
            .catch(err => { console.log(err) }) 
            .then(function () {
                postReplyModal.hide(); 
            });
        }
    }
    // 댓글 수정 함수
    function updateReply(event) {
        const replyId = modalReplyId.value;
        const replyContent = modalReplyContent.value;

        if (replyContent == '') {
            alert('댓글 내용은 반드시 입력');
            return;
        }

        const result = confirm('정말 수정하시겠습니까?');
        if (result) {
            const data = { replyContent: replyContent };
            axios

            .put('/api/reply/' + replyId, data) 
            .then(response => {
                alert('# 댓글 수정 성공');
                readAllReplies();
                updateReplyCount();
             })
            .catch(err => { console.log(err) })
            .then(function () {
                postReplyModal.hide();
            });
        }
    }
```
> PostReplyController.java 일부
```java
    // 댓글 삭제
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Integer> deleteReply(@PathVariable Integer replyId) {
        log.info("deleteReply(replyId={})", replyId);
        
        Integer result = replyService.delete(replyId);
        return ResponseEntity.ok(result);
    }
    
    // 댓글 수정
    @PutMapping("/{replyId}")
    public ResponseEntity<Integer> updateReply(
            @PathVariable Integer replyId,
            @RequestBody ReplyUpdateDto dto) {
        log.info("updateReply(replyId={}, dto={})", replyId, dto);
        dto.setReplyId(replyId); 

        Integer result = replyService.update(dto);
        return ResponseEntity.ok(result);
    }
```
- 메인 홈 화면에서 DB 자료 불러오기

> HomeController.java 일부
```java
        // 전체 책 별점순 1~8위
        List<Book> list = homeService.readAllRankingOrderByBookScore();

        // 분야별 별점순/리뷰많은 순 순위(별점순)
        String category = "";

        // 경제/경영
        category = "경제/경영";
        List<Book> economyScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);
        
        // 인문
        category = "인문";
        List<Book> humanitiesScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);

        // 소설
        category = "소설";
        List<Book> fictionScoreList = homeService.readAllRankingCategoryOrderByBookScore(category);
```
> HomeService.java 일부
```java
    // 전체 별점 Top 4
    @Transactional(readOnly = true)
    public List<Book> readAllRankingOrderByBookScore() {
        List<Book> list = null;
        list = bookRepository.findTop4ByOrderByBookScoreDesc();
        
        return list;
    }

    // 카테고리별 별점 Top 4
    @Transactional(readOnly = true)
    public List<Book> readAllRankingCategoryOrderByBookScore(String category) {
        List<Book> list = new ArrayList<>();
        list = categoryRepository.findTop4ByCategoryOrderByBookScoreDesc(category);
        
        return list;
    }
```
> CategoryRepository.java 일부
```java
    // 메인에서 보여줄 Top8 & category별 & 별점순/리뷰순
    List<Book> findTop4ByCategoryOrderByBookScoreDesc(String category);
    List<Book> findTop4ByCategoryOrderByPostCountDesc(String category);
```
- 메인 홈(검색 기능, 정렬 기능, 페이징 처리)

 ##### 검색 기능
 
> SearchController.java 일부
```java
    @GetMapping("/s")
    public String search(SearchQueryDataDto dto, Model model, @PageableDefault(size = 5) Pageable pageable) {
        String type = dto.getType();
        String keyword = dto.getKeyword();
        String order = dto.getOrder();
        // 리뷰 수 카운트
        List<SearchListDto> reviewCount = new ArrayList<>(); 
        for (Book b : noPageSearchList) {
            Integer count = postService.countPostByBookId(b.getBookId());
            
            SearchListDto reviewElement = SearchListDto.builder().BookId(b.getBookId()).reviewCount(count).build();
            reviewCount.add(reviewElement);
        }

                  '
                  '
                  '
            model.addAttribute("searchList", searchList);
            model.addAttribute("storedType", type);
            model.addAttribute("storedKeyword", keyword);
            model.addAttribute("storedOrder", order);
            model.addAttribute("reviewCount", reviewCount);
            return "/search";
}
```

> SearchService.java 일부
```java
    @Transactional(readOnly = true)
    public List<Book> search(String type, String keyword, String order){
        List<Book> list = null;
        if (order.equals("0")) { // 정렬 기준이 없을 때
            if(type.equals("all")) { // 통합 검색
                return list = searchRepository.unifiedSearchByKeyword(keyword);
            }  else if (type.equals("do")) { // 국내 도서 검색
                return list = searchRepository.domesticSearchByKeyword(keyword);
            } else if (type.equals("fo")) { // 외국 도서 검색
                return list = searchRepository.foreignSearchByKeyword(keyword);
            } else if (type.equals("au")) { // 저자 검색
                return list = searchRepository.authorSearchByKeyword(keyword);
            }
        }
       return list;
}
```

> SearchRepository.java 일부
```java
    // 여기서부터는 리뷰순, 조회순에 필요한 쿼리문 시작(List 타입 리턴하는 쿼리문)
    // 통합(제목, 저자, 출판사, 인트로) 검색
    @Query(
            "select b from BOOKS b"
           + " where lower(b.bookName) like lower ('%' || :keyword || '%')"
           + " or lower(b.author) like lower ('%' || :keyword || '%')"
           + " or lower(b.publisher) like lower ('%' || :keyword || '%')"
           + " or lower(b.bookIntro) like lower ('%' || :keyword || '%') order by b.bookName desc"
    )
    List<Book> unifiedSearchByKeyword(@Param(value = "keyword") String keyword);

    // 국내 도서 검색
    @Query(
            "select b from BOOKS b"
           + " where b.bookgroup = '국내도서'"
           + " and (lower(b.bookName) like lower ('%' || :keyword || '%')"
           + " or lower(b.author) like lower ('%' || :keyword || '%')"
           + " or lower(b.publisher) like lower ('%' || :keyword || '%')"
           + " or lower(b.bookIntro) like lower ('%' || :keyword || '%'))"
           + " order by b.bookId desc"
    )
    List<Book> domesticSearchByKeyword(@Param(value = "keyword") String keyword);
    
    // 외국 도서 검색
    @Query(
            "select b from BOOKS b"
                    + " where b.bookgroup = '외국도서'"
                    + " and (lower(b.bookName) like lower ('%' || :keyword || '%')"
                    + " or lower(b.author) like lower ('%' || :keyword || '%')"
                    + " or lower(b.publisher) like lower ('%' || :keyword || '%')"
                    + " or lower(b.bookIntro) like lower ('%' || :keyword || '%'))"
                    + " order by b.bookId desc"
            )
    List<Book> foreignSearchByKeyword(@Param(value = "keyword") String keyword);
    
    // 저자(author) 검색
    @Query(
            "select b from BOOKS b"
           + " where lower(b.author) like lower ('%' || :keyword || '%') order by b.author desc"
    )
    List<Book> authorSearchByKeyword(@Param(value = "keyword") String keyword);
```

> layout.html 일부
```java
    <form th:action="@{ /search/s }">
    <!-- 검색 타입 -->
    <select id="type" name="type" class="selectedType">
            <option value="all">통합검색</option>
            <option value="do">국내도서</option>
            <option value="fo">외국도서</option>
            <option value="au">저자</option>
    </select>
      <!-- 검색어 입력 창 -->
        <input type="search" class="searchText" id="keyword" name="keyword" />
        <input type="hidden" id="order" name="order" value="0"/>
      <!-- 검색 버튼 -->
      <button type="submit" style="border:none; background-color:black; color:white; margin-right:40px; margin-left:20px;"  onclick="nothingKeywordCheck();">
      <i class="fa fa-search" id="btnSearch"></i></button> 
```

 ##### 정렬 기능
 
> search.html 일부
```java
        <div>총 검색된 결과 : <span th:text="${searchList.totalElements}"></span></div>
        <!-- 검색 상단바 START -->
        <div class="selectRearrange d-inline-flex px-2 my-1 border rounded text-secondary" style="padding: 10px; padding-left: 15px;" align ="left"> 
         <strong class="my-2">
          <a th:href="@{ /search/s?type={type}&keyword={rekeyword}&order=hitCount (type = ${ storedType }, rekeyword = ${ storedKeyword }) }" class="w3-bar-item w3-hover-white w3-round w3-button" style="font-size: 15px; text-align:center; padding-top:5px; color:LightSlateGray; font-weight:bold;">인기도순</a>
          <a th:href="@{ /search/s?type={type}&keyword={rekeyword}&order=publishedDate (type = ${ storedType }, rekeyword = ${ storedKeyword }) }" class="w3-bar-item w3-hover-white w3-round w3-button" style="font-size: 15px; text-align:center; padding-top:5px; color:LightSlateGray; font-weight:bold;">신상품순</a>
          <a th:href="@{ /search/s?type={type}&keyword={rekeyword}&order=accuracy (type = ${ storedType }, rekeyword = ${ storedKeyword }) }" class="w3-bar-item w3-hover-white w3-round w3-button" style="font-size: 15px; text-align:center; padding-top:5px; color:LightSlateGray; font-weight:bold;">정확도순</a>
          <a th:href="@{ /search/s?type={type}&keyword={rekeyword}&order=lowPrice (type = ${ storedType }, rekeyword = ${ storedKeyword }) }" class="w3-bar-item w3-hover-white w3-round w3-button" style="font-size: 15px; text-align:center; padding-top:5px; color:LightSlateGray; font-weight:bold;">최저가순</a>
          <a th:href="@{ /search/s?type={type}&keyword={rekeyword}&order=highPrice (type = ${ storedType }, rekeyword = ${ storedKeyword }) }" class="w3-bar-item w3-hover-white w3-round w3-button" style="font-size: 15px; text-align:center; padding-top:5px; color:LightSlateGray; font-weight:bold;">최고가순</a>
          <a th:href="@{ /search/s?type={type}&keyword={rekeyword}&order=highScore (type = ${ storedType }, rekeyword = ${ storedKeyword }) }" class="w3-bar-item w3-hover-white w3-round w3-button" style="font-size: 15px; text-align:center; padding-top:5px; color:LightSlateGray; font-weight:bold;">별점순</a>
          <a th:href="@{ /search/s?type={type}&keyword={rekeyword}&order=reviewCount (type = ${ storedType }, rekeyword = ${ storedKeyword }) }" class="w3-bar-item w3-hover-white w3-round w3-button" style="font-size: 15px; text-align:center; padding-top:5px; color:LightSlateGray; font-weight:bold;">리뷰순</a>
          </strong>
        </div>
```

> SearchController.java 일부
```java
    // 검색 기능 - 검색 결과 정렬(type, keyword를 가지고 다시 order by ?, ?부분만 원하는 order에 따라 바꿔서 검색
    @GetMapping("/s")
    public String search(SearchQueryDataDto dto, Model model, @PageableDefault(size = 5) Pageable pageable) {
        String type = dto.getType();
        String keyword = dto.getKeyword();
        String order = dto.getOrder();
        List<Book> noPageSearchList = searchService.search(type, keyword, order);
                              '
                              '
                              '
        // 리뷰순, 조회수 순 정렬할 때 List 직접 재정렬
        if (order.equals("reviewCount")) { // 리뷰순 정렬
            List<SearchReadDto> list = new ArrayList<>();
            // book리스트 + 리뷰개수리스트 = 합친 리뷰 갯수를 포함하는 bookId정보를 생성
            for (Book s : noPageSearchList) {
                for (SearchListDto l : reviewCount) {
                    if (s.getBookId().equals(l.getBookId())) {
                        SearchReadDto listElement = SearchReadDto.builder().bookId(s.getBookId()).bookName(s.getBookName())
                                .author(s.getAuthor()).publisher(s.getPublisher()).publishedDate(s.getPublishedDate())
                                .prices(s.getPrices()).bookImage(s.getBookImage()).reviewCount(l.getReviewCount())
                                .bookgroup(s.getBookgroup()).category(s.getCategory())
                                .build();
                        list.add(listElement);
                    }
                }
            }
            
            // 리뷰순으로 오름차순 정렬
            list.sort(new Comparator<SearchReadDto>() {
                @Override
                public int compare(SearchReadDto arg0, SearchReadDto arg1) {
                    int reviewCount0 = arg0.getReviewCount();
                    int reviewCount1 = arg1.getReviewCount();
                    
                    if(reviewCount0 == reviewCount1) return 0;
                    else if(reviewCount0 > reviewCount1) return -1;
                    else return 1;
                }
            });
         }
                              '
                              '
                              '
            model.addAttribute("searchList", reviewList);
            model.addAttribute("storedType", type);
            model.addAttribute("storedKeyword", keyword);
            model.addAttribute("storedOrder", order);
            model.addAttribute("reviewCount", reviewCount);
            return "/search";
}
```

> SearchService.java 일부
```java
    @Transactional(readOnly = true)
    public List<Book> search(String type, String keyword, String order){
        List<Book> list = null;
        // 드랍 다운 검색 타입(value 속성): 리뷰순, 조회순
        if (order.equals("0")) { // 정렬 기준이 없을 때
            if(type.equals("all")) { // 통합 검색
                return list = searchRepository.unifiedSearchByKeyword(keyword);
            }  else if (type.equals("do")) { // 국내 도서 검색
                return list = searchRepository.domesticSearchByKeyword(keyword);
            } else if (type.equals("fo")) { // 외국 도서 검색
                return list = searchRepository.foreignSearchByKeyword(keyword);
            } else if (type.equals("au")) { // 저자 검색
                return list = searchRepository.authorSearchByKeyword(keyword);
            }
        } else if (order.equals("highPrice")) { // 최고가순 정렬
            if(type.equals("all")) { // 통합 검색
                return list = searchRepository.researchOrderAllByHighPrice(keyword);
            }  else if (type.equals("do")) { // 국내 도서 검색
                String orderType = "국내도서";
                return list = searchRepository.researchOrderByHighPrice(keyword, orderType);
            } else if (type.equals("fo")) { // 외국 도서 검색
                String orderType = "외국도서";
                return list = searchRepository.researchOrderByHighPrice(keyword, orderType);
            } else if (type.equals("au")) { // 저자 검색
                String orderType = "저자";
                return list = searchRepository.researchOrderByHighPrice(keyword, orderType);
            }
        } else if (order.equals("lowPrice")) { // 최저가순 정렬
            if(type.equals("all")) { // 통합 검색
                return list = searchRepository.researchOrderAllByLowPrice(keyword);
            }  else if (type.equals("do")) { // 국내 도서 검색
                String orderType = "국내도서";
                return list = searchRepository.researchOrderByLowPrice(keyword, orderType);
            } else if (type.equals("fo")) { // 외국 도서 검색
                String orderType = "외국도서";
                return list = searchRepository.researchOrderByLowPrice(keyword, orderType);
            } else if (type.equals("au")) { // 저자 검색
                String orderType = "저자";
                return list = searchRepository.researchOrderByLowPrice(keyword, orderType);
            }
         }
                                 '
                                 '
                                 '
    return list;
}
```

> SearchRepository.java 일부
```java
    // 최고가순 정렬
    @Query(
            "select b from BOOKS b"
                    + " where lower(b.bookName) like lower ('%' || :keyword || '%')"
                    + " or lower(b.author) like lower ('%' || :keyword || '%')"
                    + " or lower(b.publisher) like lower ('%' || :keyword || '%')"
                    + " or lower(b.bookIntro) like lower ('%' || :keyword || '%') order by b.prices desc"
            )
    List<Book> researchOrderAllByHighPrice(@Param(value = "keyword") String keyword);
    @Query(
            "select b from BOOKS b"
                    + " where b.bookgroup = :orderType"
                    + " and (lower(b.bookName) like lower ('%' || :keyword || '%')"
                    + " or lower(b.author) like lower ('%' || :keyword || '%')"
                    + " or lower(b.publisher) like lower ('%' || :keyword || '%')"
                    + " or lower(b.bookIntro) like lower ('%' || :keyword || '%')) order by b.prices desc"
    )
    List<Book> researchOrderByHighPrice(@Param(value = "keyword") String keyword, 
                                        @Param(value = "orderType") String orderType);
```

 ##### 페이징 처리

> search.html 일부
```java
    <!-- 페이징 시작 -->
        <nav aria-label="Page navigation example">
            <ul class="pagination justify-content-center" th:if="${searchList.totalPages != 0}">
                <li class="page-item" th:classappend="${1 == searchList.pageable.pageNumber + 1} ? 'disabled'">
                    <a class="page-link" th:href="@{/search/s?type={type}&keyword={rekeyword}&order={reorder}
                     (type = ${ storedType }, rekeyword = ${ storedKeyword }, reorder = ${ storedOrder }, page = ${searchList.pageable.pageNumber - 1})}">Previous</a>
                </li>
                <li class="page-item" th:classappend="${i == searchList.pageable.pageNumber + 1} ? 'disabled'" 
                    th:each="i : ${#numbers.sequence(startPage, endPage)}"><a class="page-link" th:href="@{/search/s?type={type}&keyword={rekeyword}&order={reorder}
                     (type = ${ storedType }, rekeyword = ${ storedKeyword }, reorder = ${ storedOrder }, page = ${i - 1})}" th:text="${i}">1</a>
                </li>
                <li class="page-item" th:classappend="${searchList.totalPages == searchList.pageable.pageNumber + 1} ? 'disabled'">
                    <a class="page-link" th:href="@{/search/s?type={type}&keyword={rekeyword}&order={reorder}
                     (type = ${ storedType }, rekeyword = ${ storedKeyword }, reorder = ${ storedOrder }, page = ${searchList.pageable.pageNumber + 1})}">Next</a>
                </li>
            </ul>
        </nav>
    <!-- 페이징 끝 -->
```

> SearchController.java 일부
```java
    @GetMapping("/s")
    public String search(SearchQueryDataDto dto, Model model, @PageableDefault(size = 5) Pageable pageable) {
        // 정렬할 리스트 or 페이지화 할 리스트 
        Page<Book> searchList = searchService.search(type, keyword, order, pageable);

            // 리스트를 Page로 변환 - 그래야 정상적으로 페이징이됨.
            int start= (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), list.size());            
            Page<SearchReadDto> reviewList = new PageImpl<>(list.subList(start, end), pageable, list.size());

            // 리뷰순에 적용되는 시작페이지, 끝 페이지
            startPage = Math.max(1, reviewList.getPageable().getPageNumber() - 4);
            endPage = Math.min(reviewList.getTotalPages(), reviewList.getPageable().getPageNumber() + 4);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            // 조회순에 적용되는 시작페이지, 끝 페이지
            startPage = Math.max(1, hitList.getPageable().getPageNumber() - 4);
            endPage = Math.min(hitList.getTotalPages(), hitList.getPageable().getPageNumber() + 4);
            // 페이징 - 시작페이지, 끝 페이지 (신상품순, 최저가순, 최고가순)
            startPage = Math.max(1, searchList.getPageable().getPageNumber() - 3);
            endPage = Math.min(searchList.getTotalPages(), searchList.getPageable().getPageNumber() + 3);
}
```

> SearchService.java 일부
```java
    @Transactional(readOnly = true)
    public Page<Book> search(String type, String keyword, String order, Pageable pageable){
        Page<Book> list = null;
                                 '
                                 '
                                 '
        return list;
    }
```

> SearchRepository.java 일부
```java
    // 신상품순, 최저가순, 최고가순에 필요한 쿼리문 시작(Page 타입 리턴하는 쿼리문)
    // 통합(제목, 저자, 출판사, 인트로) 검색
    @Query(
            "select b from BOOKS b"
           + " where lower(b.bookName) like lower ('%' || :keyword || '%')"
           + " or lower(b.author) like lower ('%' || :keyword || '%')"
           + " or lower(b.publisher) like lower ('%' || :keyword || '%')"
           + " or lower(b.bookIntro) like lower ('%' || :keyword || '%') order by b.bookName desc"
    )
    Page<Book> unifiedSearchByKeyword(@Param(value = "keyword") String keyword, Pageable pageable);

    // 국내 도서 검색
    @Query(
            "select b from BOOKS b"
           + " where b.bookgroup = '국내도서'"
           + " and (lower(b.bookName) like lower ('%' || :keyword || '%')"
           + " or lower(b.author) like lower ('%' || :keyword || '%')"
           + " or lower(b.publisher) like lower ('%' || :keyword || '%')"
           + " or lower(b.bookIntro) like lower ('%' || :keyword || '%'))"
           + " order by b.bookId desc"
    )
    Page<Book> domesticSearchByKeyword(@Param(value = "keyword") String keyword, Pageable pageable);
```

- 조회수(책 상세보기 조회수, 블로그 글 조회수, 페이지마다 쿠키를 적용)
  
 ##### 조회수(book, post)

> HitController.java 일부
```java
    // 쿠키써서 조회수 어뷰징 방지
    // 쿠키 재적용 시간은 setMaxAge에서 시간 조절 가능, 일단 30분으로 설정
    // bookDetail 관련 조회수
    @GetMapping("/viewCount")
    private void viewCountUp(Integer bookId, String username, HttpServletRequest request, HttpServletResponse response) {
        if (username == null || username.equals("")) { // 비회원은 전부 anonymoususer로 처리
            username = "anonymoususer";
        }
        Cookie viewCookie = null; // 쿠키값 정의
        Cookie[] cookies = request.getCookies(); // 클라이언트에서 보낸 데이터에서 쿠키 값을 가져옴
        if (cookies != null) { // 쿠키들이 있다면,
            for (Cookie cookie : cookies) { // 있는 쿠키들 중에 bookDetail 쿠키가 있으면 viewCookie를 쿠키로 저장.
                if (cookie.getName().equals("bookDetailViewCount")) {
                    viewCookie = cookie;
                }
            }
        }
        
        // 만들어진 쿠키가 없을 때
        if (viewCookie != null) { // view 쿠키가 있다면, 
            if (!viewCookie.getValue().contains("[" + username + "_" + bookId.toString() + "]")) { // [username_bookid] 라는 형태의 쿠키로 저장
                bookHitsService.viewCountUp(bookId);
                viewCookie.setValue(viewCookie.getValue() + "_[" + username + "_" + bookId + "]");
                viewCookie.setPath("/");
                viewCookie.setMaxAge(60 * 30);
                response.addCookie(viewCookie);
            }
        } else { // view 쿠키가 없을 경우 쿠키를 만들고 조회수 1을 증가시켜주기
            bookHitsService.viewCountUp(bookId);
            Cookie newCookie = new Cookie("bookDetailViewCount","[" + username + "_" + bookId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 30);
            response.addCookie(newCookie);
        }
    }
```

> HitsService.java 일부
```java
    // 조회수 DB에 저장
    @Transactional
    public void viewCountUp(Integer bookId) {
        BookHits isExist = bookHitsRepository.findByBookId(bookId);
        BookHits dto = null;
        log.info("isexist{}", isExist);
        if (isExist != null && isExist.getBookId() != null) {
            dto = bookHitsRepository.findByBookId(bookId);
            log.info("이미 있는 저장이라면 저장 전{}", dto);
            isExist.update(bookId, isExist.getHit()+1);
            log.info("이미 있는 저장이라면 저장 후{}", dto);
        } else {
            dto = BookHits.builder().hit(1).bookId(bookId).build();
            log.info("처음 저장이라면 저장 전{}", dto);
            bookHitsRepository.save(dto);
            log.info("처음 저장이라면 저장 후{}", dto);
        }
    }
```
> 책 상세보기 조회수 증가 script
```java
    <!-- 조회수 증가(책 상세보기, 리뷰 상세보기) -->
    <script>
    function viewHitUp(bookId, username){
        axios.get('/viewCount', {params: {bookId : bookId, username : username}})
           .then(response => {
               console.log(response);
               return true;
           })
           .catch(err =>{
               console.log(err);
           });
    }
    </script>
```
