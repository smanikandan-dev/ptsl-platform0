package com.itextos.beacon.platform.elasticsearchutil.data;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import com.itextos.beacon.commonlib.constants.MiddlewareConstant;
import com.itextos.beacon.commonlib.message.BaseMessage;
import com.itextos.beacon.platform.elasticsearchutil.types.EsConstant;
import com.itextos.beacon.platform.elasticsearchutil.types.EsCreateTimeStamp;
import com.itextos.beacon.platform.elasticsearchutil.types.EsOperation;
import com.itextos.beacon.platform.elasticsearchutil.utility.EsUtility;

public class AgingInsert
        extends
        AbstractEsInmemoryCollection
{

    public AgingInsert()
    {
        super(EsOperation.AGING_INSERT);
    }

    @Override
    IndexRequest getInsertUpdateRequest(
            String aEsIndexName,
            BaseMessage aMessage)
    {
        final String       messageId    = aMessage.getValue(MiddlewareConstant.MW_MESSAGE_ID);
        final IndexRequest indexRequest = new IndexRequest(aEsIndexName);
        indexRequest.id(messageId);

        final String commonJson = EsUtility.getJsonContent(aMessage, EsConstant.AGING_INSERT_FIELDS, EsCreateTimeStamp.AGING_DN_CTIME);
        indexRequest.source(commonJson, XContentType.JSON);
        return indexRequest;
    }

}