/*
 * Copyright (c) 2008-2009, Motorola, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of the Motorola, Inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.bluetooth.opp;

import java.util.regex.Pattern;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Config;
import android.util.Log;

/**
 * Bluetooth OPP internal constants definition
 */
public class Constants {
    /** Tag used for debugging/logging */
    public static final String TAG = "BluetoothShareManager";

    /**
     * The intent that gets sent when the service must wake up for a retry Note:
     * only retry Outbound transfer
     */
    public static final String ACTION_RETRY = "android.btopp.intent.action.RETRY";

    /** the intent that gets sent when clicking a successful transfer */
    public static final String ACTION_OPEN = "android.btopp.intent.action.OPEN";

    /** the intent that gets sent when clicking an incomplete/failed transfer */
    public static final String ACTION_LIST = "android.btopp.intent.action.LIST";

    /**
     * the intent that gets sent when deleting the notification of a completed
     * transfer
     */
    public static final String ACTION_HIDE = "android.btopp.intent.action.HIDE";

    /**
     * the intent that gets sent when clicking a incoming file confirm
     * notification
     */
    public static final String ACTION_INCOMING_FILE_CONFIRM = "android.btopp.intent.action.CONFIRM";

    public static final String THIS_PACKAGE_NAME = "com.android.bluetooth";

    /**
     * The column that is used to remember whether the media scanner was invoked
     */
    public static final String MEDIA_SCANNED = "scanned";

    public static final int MEDIA_SCANNED_NOT_SCANNED = 0;

    public static final int MEDIA_SCANNED_SCANNED_OK = 1;

    public static final int MEDIA_SCANNED_SCANNED_FAILED = 2;

    /**
     * The MIME type(s) of we could share to other device.
     */
    /*
     * TODO: define correct type list
     */
    public static final String[] ACCEPTABLE_SHARE_OUTBOUND_TYPES = new String[] {
        "image/*",
    };

    /**
     * The MIME type(s) of we could not share to other device. TODO: define
     * correct type list
     */
    public static final String[] UNACCEPTABLE_SHARE_OUTBOUND_TYPES = new String[] {
        "virus/*",
    };

    /**
     * The MIME type(s) of we could accept from other device. TODO: define
     * correct type list
     */
    public static final String[] ACCEPTABLE_SHARE_INBOUND_TYPES = new String[] {
        "image/*",
    };

    /**
     * The MIME type(s) of we could not accept from other device. TODO: define
     * correct type list
     */
    public static final String[] UNACCEPTABLE_SHARE_INBOUND_TYPES = new String[] {
        "text/x-vcalendar",
        "text/x-vcard",
    };

    /** Where we store Bluetooth received files on the external storage */
    public static final String DEFAULT_STORE_SUBDIR = "/bluetooth";

    /**
     * Enable verbose logging - use with
     * "setprop log.tag.BluetoothShareManager VERBOSE"
     */
    private static final boolean LOCAL_LOGV = false;

    public static final boolean LOGV = LOCAL_LOGV && Log.isLoggable(TAG, Log.VERBOSE);

    /** Enable super-verbose logging */
    private static final boolean LOCAL_LOGVV = false;

    public static final boolean LOGVV = LOCAL_LOGVV && LOGV;

    // public static final boolean LOGVV = true;

    /** use TCP socket instead of Rfcomm Socket to develop */
    public static final boolean USE_TCP_DEBUG = false;

    /** Test TCP socket port */
    public static final int TCP_DEBUG_PORT = 6500;

    /** use emulator to debug */
    public static final boolean USE_EMULATOR_DEBUG = false;

    public static final int MAX_RECORDS_IN_DATABASE = 20;

    public static final int BATCH_STATUS_PENDING = 0;

    public static final int BATCH_STATUS_RUNNING = 1;

    public static final int BATCH_STATUS_FINISHED = 2;

    public static final int BATCH_STATUS_FAILED = 3;

    public static final String BLUETOOTHOPP_NAME_PREFERENCE = "btopp_names";

    public static final String BLUETOOTHOPP_CHANNEL_PREFERENCE = "btopp_channels";

    public static String filename_SEQUENCE_SEPARATOR = "-";

    public static void updateShareStatus(Context context, int id, int status) {
        Uri contentUri = Uri.parse(BluetoothShare.CONTENT_URI + "/" + id);
        ContentValues updateValues = new ContentValues();
        updateValues.put(BluetoothShare.STATUS, status);
        context.getContentResolver().update(contentUri, updateValues, null, null);
        Constants.sendIntentIfCompleted(context, contentUri, status);
    }

    /*
     * This function should be called whenever transfer status change to
     * completed.
     */
    public static void sendIntentIfCompleted(Context context, Uri contentUri, int status) {
        if (BluetoothShare.isStatusCompleted(status)) {
            Intent intent = new Intent(BluetoothShare.TRANSFER_COMPLETED_ACTION);
            intent.setClassName(THIS_PACKAGE_NAME, BluetoothOppReceiver.class.getName());
            intent.setData(contentUri);
            context.sendBroadcast(intent);
        }
    }

    public static boolean mimeTypeMatches(String mimeType, String[] matchAgainst) {
        for (String matchType : matchAgainst) {
            if (mimeTypeMatches(mimeType, matchType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean mimeTypeMatches(String mimeType, String matchAgainst) {
        Pattern p = Pattern.compile(matchAgainst.replaceAll("\\*", "\\.\\*"),
                Pattern.CASE_INSENSITIVE);
        return p.matcher(mimeType).matches();
    }
}
