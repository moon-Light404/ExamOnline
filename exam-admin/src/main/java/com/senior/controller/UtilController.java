package com.senior.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.senior.util.CreateVerificationCode;
import com.senior.vo.CommonResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/util")
@Api(tags = "工具类接口")
public class UtilController {

    static String CODE;

    /**
     * 生成随机验证码图片
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/getCodeImg")
    @ApiOperation(value = "获取验证码图片流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "帮助前端生成验证码", required = false, dataType = "string",
                    paramType = "query")
    })
    public void getIdentifyImage(@RequestParam(required = false) String id, HttpServletResponse response)
            throws IOException {
        log.info("执行了===>UtilController中的getIdentifyImage方法");
        // 设置不缓存图片
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "No-cache");
        response.setDateHeader("Expires", 0);
        // 指定生成的响应图片
        response.setContentType("image/jpeg");
        CreateVerificationCode code = new CreateVerificationCode();
        BufferedImage image = code.getIdentifyImg();
        code.getG().dispose();
        // 将图形验证码IO流传输至前端
        ImageIO.write(image, "JPEG", response.getOutputStream());
        CODE = code.getCode();
    }

    @GetMapping("/getCode")
    @ApiOperation(value = "获取验证码")
    public CommonResult<String> getCode() {
        return new CommonResult<>(200, CODE);
    }
}
