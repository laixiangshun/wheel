package com.lxs.bigdata.es.api;

import com.lxs.bigdata.es.beans.IntelligentStoreDTO;
import com.lxs.bigdata.es.beans.IntelligentStoreVO;
import com.lxs.bigdata.es.service.IntelligentStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/es/intelligent_store/")
public class IntelligentStoreApi {

    @Autowired
    private IntelligentStoreService intelligentStoreService;

    /**
     * 搜索附近的体验店
     */
    @ResponseBody
    @RequestMapping(value = "find", method = RequestMethod.POST)
    public ResponseEntity findAllByLocaltion(@RequestBody @Valid IntelligentStoreDTO intelligentStoreDTO) {
        Page<IntelligentStoreVO> storeVOPage = intelligentStoreService.findAllByLocaltion(intelligentStoreDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("code", "1");
        result.put("msg", "成功");
        result.put("data", storeVOPage.getContent());
        result.put("pageIndex", storeVOPage.getNumber());
        result.put("pageSize", storeVOPage.getSize());
        result.put("totalPage", storeVOPage.getTotalPages());
        result.put("totals", storeVOPage.getTotalElements());
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
