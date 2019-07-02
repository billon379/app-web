package fun.billon.app.member.controller;

import fun.billon.auth.api.feign.IAuthService;
import fun.billon.common.constant.CommonStatusCode;
import fun.billon.common.exception.ParamException;
import fun.billon.common.model.ResultModel;
import fun.billon.common.util.StringUtils;
import fun.billon.member.api.feign.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 授权相关接口
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

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
     * auth
     */
    @Autowired
    private IAuthService authService;

    /**
     * 通过注册获取token
     *
     * @param appId    外部应用id            必填
     * @param paramMap paramMap.account   账号(邮箱,最长32位)    必填
     *                 paramMap.password  密码(md5编码后的数据)  必填
     * @return 注册成功后返回token
     */
    @PostMapping(value = "/token")
    public ResultModel<String> tokenByRegister(@RequestHeader(value = "appId") String appId,
                                               @RequestParam Map<String, String> paramMap) {
        ResultModel<String> resultModel = new ResultModel<>();

        String[] paramArray = new String[]{"account", "password"};
        boolean[] requiredArray = new boolean[]{true, true};
        Class[] classArray = new Class[]{String.class, String.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }

        /*
         * 用户注册
         */
        ResultModel<Integer> registerResult = memberService.register(sid, sid, paramMap);
        if (registerResult.getCode() != ResultModel.RESULT_SUCCESS) {
            resultModel.setFailed(registerResult);
            return resultModel;
        }

        /*
         * 注册成功,返回token
         */
        return authService.token(sid, sid, appId, registerResult.getData() + "", new HashMap<>(0));
    }


    /**
     * 通过登陆获取token
     *
     * @param appId    外部应用id            必填
     * @param paramMap paramMap.account   账号(邮箱,最长32位)    必填
     *                 paramMap.password  密码(md5编码后的数据)  必填
     * @return 登陆成功后返回token
     */
    @GetMapping(value = "/token")
    public ResultModel<String> tokenByLogin(@RequestHeader(value = "appId") String appId,
                                            @RequestParam Map<String, String> paramMap) {
        ResultModel<String> resultModel = new ResultModel<>();

        String[] paramArray = new String[]{"account", "password"};
        boolean[] requiredArray = new boolean[]{true, true};
        Class[] classArray = new Class[]{String.class, String.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }

        /*
         * 用户登陆
         */
        ResultModel<Integer> registerResult = memberService.login(sid, sid, paramMap);
        if (registerResult.getCode() != ResultModel.RESULT_SUCCESS) {
            resultModel.setFailed(registerResult);
            return resultModel;
        }

        /*
         * 登陆成功,返回token
         */
        return authService.token(sid, sid, appId, registerResult.getData() + "", new HashMap<>(0));
    }

    /**
     * 刷新token
     *
     * @param appId 外部应用id
     * @return 返回新申请的token
     */
    @GetMapping(value = "/token/refresh")
    public ResultModel<String> tokenRefresh(@RequestHeader(value = "appId") String appId,
                                            @RequestAttribute(value = "uid") String uid) {
        return authService.token(sid, sid, appId, uid, new HashMap<>(0));
    }

}
