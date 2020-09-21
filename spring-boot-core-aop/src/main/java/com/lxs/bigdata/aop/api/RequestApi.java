package com.lxs.bigdata.aop.api;

import com.lxs.bigdata.aop.dto.GetUserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/")
@Api(value = "操作用户", tags = "操作用户API")
public class RequestApi {

    @ResponseBody
    @RequestMapping(value = "user", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户", notes = "获取当前用户")
    public ResponseEntity getCurrentUser(@RequestBody @Valid GetUserDTO userDTO) {
        String userId = userDTO.getUserId();
        return new ResponseEntity(userId + ":" + System.currentTimeMillis(), HttpStatus.OK);
    }
}
