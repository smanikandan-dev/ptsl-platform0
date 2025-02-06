package com.itextos.beacon.interfaces.generichttpapi.processor.validate;

import com.itextos.beacon.commonlib.constants.*;
import com.itextos.beacon.commonlib.ipvalidation.IPValidator;
import com.itextos.beacon.commonlib.utility.CommonUtility;
import com.itextos.beacon.commonlib.utility.DateTimeUtility;
import com.itextos.beacon.http.generichttpapi.common.data.BasicInfo;
import com.itextos.beacon.http.generichttpapi.common.data.InterfaceRequestStatus;
import com.itextos.beacon.http.generichttpapi.common.utils.APIConstants;
import com.itextos.beacon.http.generichttpapi.common.utils.Utility;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;

public class BasicValidation {

    private static final Logger logger = LoggerFactory.getLogger(BasicValidation.class);
    private static final String API_SERVICE_ALLOW = "sms~api";

    private BasicValidation() {
    }

    public static InterfaceRequestStatus validateBasicData(
            BasicInfo aBasicInfo) {
        InterfaceRequestStatus lRequestStatus = null;

        try {
            final String lEncrypt = aBasicInfo.getEncrypt();
            final String lScheduleTime = aBasicInfo.getScheduleTime();

            if (logger.isDebugEnabled())
                logger.debug("version:  '" + aBasicInfo.getVersion() + "'");

            if ((lRequestStatus == null) && aBasicInfo.getAccessKey().isBlank()) {
                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ACCOUNT_INVALID_CREDENTIALS, "Access key field is missing");
                if (logger.isDebugEnabled())
                    logger.debug("Access key field is Missing:  '" + aBasicInfo.getAccessKey() + "'");
            }

            if (lRequestStatus == null) {
                Utility.setAccInfo(aBasicInfo);
                lRequestStatus = checkAccountStatus(aBasicInfo);
            }

            final JSONObject lJsonUserDetails = aBasicInfo.getUserAccountInfo();

            if (logger.isDebugEnabled()) {
                logger.debug("Client Account Details : '" + lJsonUserDetails + "'");
                logger.debug("Status key : " + lRequestStatus);
            }

            if (lRequestStatus == null)
                lRequestStatus = validateVersionInfo(aBasicInfo);

            if (lRequestStatus == null) {
                final boolean isApiServiceAllow = isAPIServiceAllow(lJsonUserDetails);
                if (logger.isDebugEnabled())
                    logger.debug("Is API Service Allow:  '" + isApiServiceAllow + "'");
                if (!isApiServiceAllow)
                    lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.API_SERVICE_DISABLED, null);
            }

            if (lRequestStatus == null) {
                final String isIPValidationEnable = CommonUtility.nullCheck(lJsonUserDetails.get(MiddlewareConstant.MW_IP_VALIDATION.getName()), true);
                final String lIPList = CommonUtility.nullCheck(Utility.getJSONValue(lJsonUserDetails, MiddlewareConstant.MW_IP_LIST.getName()));
                final String lCluster = CommonUtility.nullCheck(lJsonUserDetails.get(MiddlewareConstant.MW_PLATFORM_CLUSTER.getName()), true);
                final String lClientId = Utility.getJSONValue(lJsonUserDetails, MiddlewareConstant.MW_CLIENT_ID.getName());

                lRequestStatus = Utility.validateCluster(lCluster);

                if (lRequestStatus == null) {
                    final boolean isValidIP = IPValidator.getInstance().isValidIP(isIPValidationEnable, lClientId, lIPList, aBasicInfo.getCustIp());

                    if (logger.isDebugEnabled())
                        logger.debug("Is IP validation Enabled?  '" + isValidIP + "'");

                    if (!isValidIP) {
                        lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.IP_RESTRICTED, "This is Your IP :  " + aBasicInfo.getCustIp() + " IP configured in Database : " + lIPList);

                        logger.warn(" lClientId : " + lClientId + " aBasicInfo.getCustIp() : " + aBasicInfo.getCustIp() + " IP Blacklist configured in Database : " + lIPList);
                    }
                } else
                    logger.error("Access Violation for user " + lJsonUserDetails.get(MiddlewareConstant.MW_USER) + " Clusster '" + lCluster + "'");
            }

            if ((lRequestStatus == null) && !("".equals(lScheduleTime))) {
                final InterfaceStatusCode validScheduledTime = isValidScheduleTime(aBasicInfo, lScheduleTime);

                if (logger.isDebugEnabled())
                    logger.debug("Schedule time  '" + lScheduleTime + "' Schedule time validation status  '" + validScheduledTime + "'");

                if (validScheduledTime != InterfaceStatusCode.SUCCESS)
                    lRequestStatus = new InterfaceRequestStatus(validScheduledTime, null);
            }

            if ((lRequestStatus == null) && (!"".equals(lEncrypt) && !"1".equals(lEncrypt) && !"0".equals(lEncrypt))) {
                if (logger.isDebugEnabled())
                    logger.debug("Encryption option invalid  '" + lEncrypt + "'");

                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ENCRYPTION_OPTION_INVALID, null);
            }

            if (lRequestStatus == null) {
                if (logger.isDebugEnabled())
                    logger.debug("Common validation success'");

                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.SUCCESS, "Request Accepted");
                aBasicInfo.setRequestStatus(lRequestStatus);
            }
        }
        catch (final Exception e) {
            logger.error("Exception while validating common object", e);
            lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.INTERNAL_SERVER_ERROR, "Internal Error");
        }

        lRequestStatus.setResponseTime(System.currentTimeMillis());
        return lRequestStatus;
    }

    private static InterfaceRequestStatus validateVersionInfo(
            BasicInfo aBasicInfo) {
        InterfaceRequestStatus lRequestStatus = null;

        final String lClientId = aBasicInfo.getClientId();

        final boolean isVersionRequired = Utility.isIgnoreVersion(lClientId);

        if (logger.isDebugEnabled())
            logger.debug("Version required for the client :'" + lClientId + "', status:" + isVersionRequired);

        if (!isVersionRequired) {

            if (aBasicInfo.getVersion().isBlank()) {
                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.API_VERSION_INVALID, "Version field is missing");
                if (logger.isDebugEnabled())
                    logger.debug("Version Request Parameter is Missing");
            }

            if (lRequestStatus == null) {
                final boolean lValidateVersion = isValidVersion(aBasicInfo.getVersion());
                if (logger.isDebugEnabled())
                    logger.debug("Is validate version:  '" + lValidateVersion + "'");

                if (!lValidateVersion)
                    lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.API_VERSION_INVALID, null);
            }
        }

        return lRequestStatus;
    }

    private static boolean isAPIServiceAllow(
            JSONObject aJsonUserDetails) {
        return CommonUtility.isEnabled(CommonUtility.nullCheck(aJsonUserDetails.get(API_SERVICE_ALLOW)));
    }

    private static InterfaceRequestStatus checkAccountStatus(
            BasicInfo aBasicInfo) {
        InterfaceRequestStatus lRequestStatus = null;
        final AccountStatus lAccountStatus = aBasicInfo.getAccountStatus();

        switch (lAccountStatus) {
            case ACTIVE:
                if (aBasicInfo.getUserAccountInfo() == null) {
                    lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ACCOUNT_INVALID_CREDENTIALS, "Invalid Credentials");
                    if (logger.isDebugEnabled())
                        logger.debug("Invalid user access key");
                }
                break;

            case DEACTIVATED:
                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ACCOUNT_DEACTIVATED, "Account Deactivated");
                break;

            case INACTIVE:
                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ACCOUNT_INACTIVE, "Account Inactive");
                break;

            case INVALID:
                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ACCOUNT_INVALID_CREDENTIALS, "Account Invalid");
                break;

            case SUSPENDED:
                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ACCOUNT_SUSPENDED, "Account Suspended");
                break;

            case EXPIRY:
                lRequestStatus = new InterfaceRequestStatus(InterfaceStatusCode.ACCOUNT_EXPIRED, "Account Expired");
                break;

            default:
                break;
        }

        if (logger.isDebugEnabled())
            logger.debug((lRequestStatus != null) ? "Account failure Reason " + lRequestStatus.getStatusDesc() : "Account Check Status :" + InterfaceStatusCode.SUCCESS);
        return lRequestStatus;
    }

    public static boolean isValidVersion(
            String aVersion) {
        return Arrays.stream(APIConstants.ALLOW_VERSIONS).anyMatch(aVersion::equals);
    }

    private static InterfaceStatusCode isValidScheduleTime(
            BasicInfo aBasicInfo,
            String aScheduleAt) {
        if (logger.isDebugEnabled())
            logger.debug("Schedule Time : '" + aScheduleAt + "'");

        Date scheduleDate = null;

        try {
            final boolean isAccScheduleFeatureEnable = CommonUtility.isEnabled(CommonUtility.nullCheck(aBasicInfo.getUserAccountInfo().get(MiddlewareConstant.MW_IS_SCHEDULE_ALLOW.getName()), true));

            if (!isAccScheduleFeatureEnable)
                return InterfaceStatusCode.SCHEDULE_OPTION_DISABLE;

            scheduleDate = DateTimeUtility.getDateFromString(aScheduleAt, DateTimeFormat.DEFAULT);

            if (scheduleDate == null)
                return InterfaceStatusCode.SCHEDULE_INVALID_TIME;

            final String timeZone = CommonUtility.nullCheck(aBasicInfo.getUserAccountInfo().get(MiddlewareConstant.MW_TIME_ZONE.getName()));

            if (logger.isDebugEnabled())
                logger.debug("Timezone : '" + timeZone + "'");

            if (!"".equals(timeZone)) {
                scheduleDate = Utility.changeScheduleTimeToGivenOffset(timeZone, aBasicInfo);

                if (logger.isDebugEnabled())
                    logger.debug("Common validation converting to IST : '" + scheduleDate + "'");
            }
        }
        catch (final Exception pe) {
            if (logger.isDebugEnabled())
                logger.debug("Exception while parsing the date for format : '" + DateTimeFormat.DEFAULT + "'. Date String : '" + aScheduleAt + "'", pe);
            return InterfaceStatusCode.SCHEDULE_INVALID_TIME;
        }

        return isValidScheduleTimeLocally(scheduleDate);
    }

    private static InterfaceStatusCode isValidScheduleTimeLocally(
            Date aScheduleDate) {
        final int minScheduleTime = Utility.getConfigParamsValueAsInt(ConfigParamConstants.SCHEDULE_MIN_TIME);
        final int maxScheduleTime = Utility.getConfigParamsValueAsInt(ConfigParamConstants.SCHEDULE_MAX_TIME);
        final long schduleDiff = (aScheduleDate.getTime() - System.currentTimeMillis()) / DateTimeUtility.ONE_MINUTE;

        if (logger.isDebugEnabled())
            logger.debug("Schdule Time " + DateTimeUtility.getFormattedDateTime(aScheduleDate, DateTimeFormat.DEFAULT_WITH_MILLI_SECONDS) + " minScheduleTime : '" + minScheduleTime
                    + "' maxScheduleTime : '" + maxScheduleTime + "' schduleDiff : '" + schduleDiff + "'");

        if ((schduleDiff < minScheduleTime) || (schduleDiff > maxScheduleTime))
            return InterfaceStatusCode.EXCEED_SCHEDULE_TIME;
        return InterfaceStatusCode.SUCCESS;
    }

}