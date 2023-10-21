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
postReply.js 일부
```ruby
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
PostReplyController.java 일부
```ruby
    // 댓글 작성
    @PostMapping
    public ResponseEntity<Integer> registerReply(@RequestBody ReplyRegisterDto dto) {
        log.info("registerReply()");

        Integer replyWriter = replyService.create(dto);

        return ResponseEntity.ok(replyWriter);
    }
```
 #### Read
postReply.js 일부
```ruby
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
```
PostReplyController.java 일부
```ruby
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
postReply.js 일부
```ruby
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
PostReplyController.java 일부
```ruby
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

- 메인 홈(검색 기능, 정렬 기능, 페이징 처리)

- 조회수(페이지마다 쿠키를 적용)

