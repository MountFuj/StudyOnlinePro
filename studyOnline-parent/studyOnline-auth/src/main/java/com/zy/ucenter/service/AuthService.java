package com.zy.ucenter.service;

import com.zy.ucenter.model.dto.AuthParamsDto;
import com.zy.ucenter.model.dto.XcUserExt;

public interface AuthService {

 XcUserExt execute(AuthParamsDto authParamsDto);

}
