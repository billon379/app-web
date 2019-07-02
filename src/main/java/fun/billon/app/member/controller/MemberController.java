package fun.billon.app.member.controller;

import fun.billon.common.constant.CommonStatusCode;
import fun.billon.common.exception.ParamException;
import fun.billon.common.model.ResultModel;
import fun.billon.common.util.StringUtils;
import fun.billon.member.api.feign.IMemberService;
import fun.billon.member.api.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户模块相关接口
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    /**
     * 内部服务id
     */
    @Value("${billon.app-web.sid}")
    private String sid;

    /**
     * member
     */
    @Autowired
    private IMemberService memberService;

    /**
     * 更新用户
     *
     * @param paramMap nickname     昵称 选填
     *                 sex          性别(1为男性;2为女性) 选填
     *                 headImgUrl   头像 选填
     * @return
     */
    @PutMapping(value = "/user")
    public ResultModel<UserModel> updateUser(@RequestParam Map<String, String> paramMap,
                                             @RequestAttribute(value = "uid") Integer uid) {
        ResultModel<UserModel> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"password", "nickname", "sex", "headImgUrl"};
        boolean[] requiredArray = new boolean[]{false, false, false, false};
        Class[] classArray = new Class[]{String.class, String.class, Integer.class, String.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        return memberService.updateUser(sid, sid, uid, paramMap);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping(value = "/user")
    public ResultModel<UserModel> user(@RequestAttribute(value = "uid") Integer uid) {
        return memberService.getUserById(sid, sid, uid);
    }

}
