package com.lxs.bigdata.es.api;

import com.alibaba.fastjson.JSONObject;
import com.lxs.bigdata.es.common.CommonResult;
import com.lxs.bigdata.es.dto.ESAddConditionDTO;
import com.lxs.bigdata.es.dto.ESConditionDTO;
import com.lxs.bigdata.es.dto.ESDeleteConditionDTO;
import com.lxs.bigdata.es.dto.ESUpdateConditionDTO;
import com.lxs.bigdata.es.service.ElasticSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "elasticsearch全文检索api")
@RestController
@RequestMapping(value = "/es/search/")
public class ElasticSearchApi {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @ApiOperation(value = "根据复杂条件搜索", notes = "根据复杂条件进行搜索，需要进行聚合操作")
    @ResponseBody
    @RequestMapping(value = "aggCondition", method = RequestMethod.POST)
    public CommonResult<String> searchByAggAndCondition(@RequestBody @Valid ESConditionDTO condition) {
        List<JSONObject> result = elasticSearchService.searchAggByCondition(condition);
        return CommonResult.success(JSONObject.toJSONString(result), "检索成功");
    }

    @ApiOperation(value = "根据复杂条件搜索", notes = "根据复杂条件进行搜索,不需要进行聚合操作")
    @ResponseBody
    @RequestMapping(value = "noAggCondition", method = RequestMethod.POST)
    public CommonResult<String> searchNoAggCondition(@RequestBody @Valid ESConditionDTO condition) {
        List<JSONObject> result = elasticSearchService.searchNoAggByCondition(condition);
        return CommonResult.success(JSONObject.toJSONString(result), "检索成功");
    }

    @ApiOperation(value = "根据复杂条件搜索", notes = "根据复杂条件进行搜索")
    @ResponseBody
    @RequestMapping(value = "condition", method = RequestMethod.POST)
    public CommonResult<String> searchCondition(@RequestBody @Valid ESConditionDTO condition) {
        List<JSONObject> result = elasticSearchService.searchByCondition(condition);
        return CommonResult.success(JSONObject.toJSONString(result), "检索成功");
    }


    @ApiOperation(value = "根据条件删除", notes = "根据ID集合删除数据")
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public CommonResult<String> deleteByCondition(@RequestBody @Valid ESDeleteConditionDTO deleteConditionDTO) {
        boolean result = elasticSearchService.bulkDelete(deleteConditionDTO);
        if (result) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @ApiOperation(value = "根据条件更新", notes = "更新数据")
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public CommonResult<String> updateByCondition(@RequestBody @Valid ESUpdateConditionDTO updateByCondition) {
        boolean result = elasticSearchService.bulkUpdate(updateByCondition);
        if (result) {
            return CommonResult.success("更新成功");
        }
        return CommonResult.failed("更新失败");
    }

    @ApiOperation(value = "根据条件更新", notes = "更新数据")
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public CommonResult<String> addByCondition(@RequestBody @Valid ESAddConditionDTO esAddConditionDTO) {
        boolean result = elasticSearchService.bulkAdd(esAddConditionDTO);
        if (result) {
            return CommonResult.success("更新成功");
        }
        return CommonResult.failed("更新失败");
    }
}
