package com.ht.authentication.controller;

import com.ht.authentication.model.JsonResult;
import com.ht.authentication.exception.CustomerException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "测试模块")
@RequestMapping("/test/")
public class TestContoller {


    @PostMapping("testParameter")
    @ApiOperation(value = "测试参数", notes = "测试参数")
    public JsonResult<String> testParameter(

            @ApiParam(value = "数组", name = "list", required = false)
            @RequestParam(value = "list", required = false)
            @NotNull.List(value = @NotNull(message = "参数不能为空"))
            @Size.List(value=@Size(min = 1,message = "数据不能为空"))
                    List<String> test,
            @ApiParam(value = "名称", name = "name", required = false)
            @RequestParam(value = "name", required = false)
            @NotBlank(message = "名称不能为空")
                    String name,
            @ApiParam(value = "年龄", name = "age", required = false)
            @RequestParam(value = "age", required = false)
            @NotNull(message = "年龄不能为空")
                    Integer age
    ) throws CustomerException {
        return JsonResult.success("成功");
    }
}
