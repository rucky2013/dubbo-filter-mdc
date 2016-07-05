/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.mdc.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.*;
import org.slf4j.MDC;


/**
 * MDCFilter
 *
 * @author breggor
 */
@Activate(group = {Constants.CONSUMER, Constants.PROVIDER})
public class MDCFilter implements Filter {
    public static final String REQ_ID = "requestId";

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!invocation.getMethodName().startsWith("$")) {
            try {
                String reqId = invocation.getAttachment(REQ_ID, "");
                if (StringUtils.isNotEmpty(reqId)) {
                    MDC.put(REQ_ID, reqId);
                } else {
                    reqId = MDC.get(REQ_ID);
                    if (StringUtils.isBlank(reqId)) {
                        throw new RuntimeException("slf4j MDC don't setting, Please setting MDC");
                    }
                    if (invocation instanceof RpcInvocation) {
                        ((RpcInvocation) invocation).setAttachment(REQ_ID, reqId);
                    }
                }
            } catch (RpcException e) {
                throw e;
            } catch (Exception e) {
                throw new RpcException(e.getMessage(), e);
            }
        }
        return invoker.invoke(invocation);
    }
}
