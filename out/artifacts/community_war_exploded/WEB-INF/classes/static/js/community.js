/**
 * 根据target类型执行请求
 * @param targetId
 * @param type
 * @param content
 */
function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容")
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),

        success: function (response) {
            console.log(response)
            if (response.code === 200) {
                //评论成功
                window.location.reload();

                //隐藏评论输入框
                // $("#comment_section").hide();
            } else {
                //评论失败

                if (response.code == 2003) {
                    //未登陆
                    if (confirm(response.message)) {
                        //确认登陆
                        window.open("https://github.com/login/oauth/authorize?client_id=6cbe2e2cbe0a53867691&redirect_uri=http://localhost:8887/callback&scope=user&state=1")

                        //在localStorage定义参数检测是否关闭新开的窗口
                        window.localStorage.setItem("closeable", true);
                    } else {
                        //取消登陆

                    }
                } else {

                    alert(response.message)
                }
            }
        },
        dataType: "json"
    });
}


/**
 * 提交回复
 */
function replyQuestion() {
    //获取问题id
    var questionId = $("#question_id").val();
    // 获取评论
    var content = $("#comment_content").val();

    comment2target(questionId, 1, content)
}

/**
 * 提交二级评论
 */
function replyComment(e) {
    //获取评论id
    var commentId = e.getAttribute("data-id");
    //获取二级评论
    var content = $("#reply_" + commentId).val();
    console.log(commentId)
    comment2target(commentId, 2, content)

}

/**
 *展开二级评论
 */
function collapseComments(e) {

    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    //获取二级评论状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        //折叠二级评论
        comments.removeClass("in")
        //移除二级评论状态
        e.removeAttribute("data-collapse")
        e.classList.remove("active")
    } else {


        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length == 1) {
            //如果还未请求过

            //请求得到后台数据
            $.getJSON("/comment/" + id, function (data) {
                console.log(data)

                $.each(data.data.reverse(), function (index, comment) {

                    var mediaLeftElement = $("<div>", {
                        class: "media-left"
                    }).append($("<img>", {
                        class: "media-object img-rounded",
                        src: comment.user.avatarUrl
                    }))


                    var mediaBodyElement = $("<div>", {
                        class: "media-body"
                    }).append($("<h5>", {
                        class: "media-heading",
                        html: comment.user.name
                    })).append($("<div>", {
                        html: comment.content
                    })).append($("<div>", {
                        class: "menu",
                    }).append($("<span>", {
                        class: "pull-right",
                        html: moment(comment.gmtModified).format("YYYY/MM/DD HH:mm:ss")
                    })))

                    var mediaElement = $("<div>", {
                        class: "media"
                    }).append(mediaLeftElement).append(mediaBodyElement)

                    var commentElement = $("<div>", {
                        class: "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement)
                    subCommentContainer.prepend(commentElement)
                })

            });
        }

        //展开二级评论
        comments.addClass("in")
        //标记二级评论状态
        e.setAttribute("data-collapse", "in")
        e.classList.add("active")
    }



}


/**
 * 选择tag
 */
function selectTag(value){
    var previous = $('#tag').val();
    if(previous){
        $('#tag').val(previous+','+value)
    }else{
        $('#tag').val(value)
    }
}
