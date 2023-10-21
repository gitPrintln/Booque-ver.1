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
- 블로그(댓글)
###### Create
PostReplyController.java 일부
    @PostMapping
    public ResponseEntity<Integer> registerReply(@RequestBody ReplyRegisterDto dto) {
        log.info("registerReply()");

        Integer replyWriter = replyService.create(dto);

        return ResponseEntity.ok(replyWriter);
    }
    
- 메인 홈 화면에서 DB 자료 불러오기

- 메인 홈(검색 기능, 정렬 기능, 페이징 처리)

- 조회수(페이지마다 쿠키를 적용)

