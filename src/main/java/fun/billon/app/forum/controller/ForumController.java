package fun.billon.app.forum.controller;

import fun.billon.common.constant.CommonStatusCode;
import fun.billon.common.exception.ParamException;
import fun.billon.common.model.ResultModel;
import fun.billon.common.util.StringUtils;
import fun.billon.forum.api.feign.IForumService;
import fun.billon.forum.api.model.ForumPostModel;
import fun.billon.forum.api.model.ForumReplyModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 论坛模块相关接口
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/forum")
public class ForumController {

    /**
     * 内部服务id
     */
    @Value("${billon.app-web.sid}")
    private String sid;

    @Resource
    private IForumService forumService;

    /**
     * 发表帖子
     *
     * @param paramMap paramMap.title           标题      必填
     *                 paramMap.content         内容      必填
     *                 paramMap.lat             纬度(gps) 选填
     *                 paramMap.lng             经度(gps) 选填
     *                 paramMap.address         位置信息   选填
     *                 paramMap.topicId         话题id    必填
     *                 paramMap.mediaList       附件      必填
     */
    @PostMapping(value = "/post")
    public ResultModel<Integer> addPost(@RequestParam Map<String, String> paramMap,
                                        @RequestAttribute(value = "uid") Integer uid) {
        ResultModel<Integer> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"title", "content", "lat", "lng", "address", "topicId", "mediaList"};
        boolean[] requiredArray = new boolean[]{true, true, false, false, false, true, true};
        Class[] classArray = new Class[]{String.class, String.class, Double.class, Double.class,
                String.class, Integer.class, String.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        paramMap.put("uid", uid + "");
        return forumService.addPost(sid, sid, paramMap);
    }

    /**
     * 发表评论
     *
     * @param postId   帖子id
     * @param paramMap paramMap.content     内容             必填
     *                 paramMap.refId       回复评论id        选填
     */
    @PostMapping(value = "/post/{postId}/reply")
    public ResultModel<Integer> addReply(@PathVariable(value = "postId") Integer postId,
                                         @RequestParam Map<String, String> paramMap,
                                         @RequestAttribute(value = "uid") Integer uid) {
        ResultModel<Integer> resultModel = new ResultModel<>();
        try {
            StringUtils.checkParam(paramMap, new String[]{"content", "refId"},
                    new boolean[]{true, false},
                    new Class[]{String.class, Integer.class}, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        paramMap.put("uid", uid + "");
        return forumService.addReply(sid, sid, postId, paramMap);
    }

    /**
     * 通过分享解锁帖子
     *
     * @param postId 帖子id 必填
     * @return
     */
    @PostMapping(value = "/post/{postId}/unlock")
    public ResultModel unlockPost(@PathVariable(value = "postId") Integer postId,
                                  @RequestAttribute(value = "uid") Integer uid) {
        return forumService.unlockPost(sid, sid, postId, uid, ForumPostModel.LIMIT_SHARE);
    }

    /**
     * 删除帖子
     *
     * @param postId 帖子id 必填
     * @return
     */
    @DeleteMapping(value = "/post/{postId}")
    public ResultModel deletePost(@PathVariable(value = "postId") Integer postId,
                                  @RequestAttribute(value = "uid") Integer uid) {
        return forumService.deletePost(sid, sid, postId, uid);
    }

    /**
     * 删除帖子评论
     *
     * @param postId  帖子id  必填
     * @param replyId 评论id  必填
     * @return
     */
    @DeleteMapping(value = "/post/{postId}/reply/{replyId}")
    public ResultModel deleteReply(@PathVariable(value = "postId") Integer postId,
                                   @PathVariable(value = "replyId") Integer replyId,
                                   @RequestAttribute(value = "uid") Integer uid) {
        return forumService.deleteReply(sid, sid, postId, replyId, uid);
    }

    /**
     * 获取帖子列表
     *
     * @param paramMap paramMap.topicId    话题id           选填
     *                 paramMap.uid        要查看的用户id    选填
     *                 paramMap.pageIndex  分页页码         选填
     *                 paramMap.pageSize   分页大小         选填
     * @return
     */
    @GetMapping(value = "/post")
    public ResultModel<List<ForumPostModel>> posts(@RequestParam Map<String, String> paramMap,
                                                   @RequestAttribute(value = "uid") Integer uid) {
        ResultModel<List<ForumPostModel>> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"topicId", "uid", "pageIndex", "pageSize"};
        boolean[] requiredArray = new boolean[]{false, false, false, false};
        Class[] classArray = new Class[]{Integer.class, Integer.class, Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        paramMap.put("currentUid", uid + "");
        return forumService.posts(sid, sid, paramMap);
    }

    /**
     * 获取帖子详情
     *
     * @param postId   帖子id                                         必填
     * @param paramMap paramMap.requireExtend   是否需要帖子扩展信息     选填
     *                 paramMap.requireReplies  是否需要评论列表        选填
     *                 paramMap.requireUnlocks  是否需要解锁记录列表     选填
     * @return
     */
    @GetMapping(value = "/post/{postId}")
    public ResultModel<ForumPostModel> postDetail(@PathVariable(value = "postId") Integer postId,
                                                  @RequestParam Map<String, String> paramMap,
                                                  @RequestAttribute(value = "uid") Integer uid) {
        ResultModel<ForumPostModel> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"requireExtend", "requireReplies", "requireUnlocks"};
        boolean[] requiredArray = new boolean[]{false, false, false};
        Class[] classArray = new Class[]{Integer.class, Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        paramMap.put("currentUid", uid + "");
        return forumService.postDetail(sid, sid, postId, paramMap);
    }

    /**
     * 获取评论列表
     *
     * @param postId   帖子id                        必填
     * @param paramMap paramMap.pageIndex  分页页码   选填
     *                 paramMap.pageSize   分页大小   选填
     * @return
     */
    @GetMapping(value = "/post/{postId}/reply")
    public ResultModel<List<ForumReplyModel>> replies(@PathVariable(value = "postId") Integer postId,
                                                      @RequestParam Map<String, String> paramMap) {
        ResultModel<List<ForumReplyModel>> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"pageIndex", "pageSize"};
        boolean[] requiredArray = new boolean[]{false, false};
        Class[] classArray = new Class[]{Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        return forumService.replies(sid, sid, postId, paramMap);
    }

}