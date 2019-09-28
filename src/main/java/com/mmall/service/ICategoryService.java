package com.mmall.service;

import com.mmall.commom.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by wuyuanyan on 2019/9/27.
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, int parentId);

    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
