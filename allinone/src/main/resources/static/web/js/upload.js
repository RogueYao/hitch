jQuery.extend({
    createUploadIframe: function (id, uri) {
        //create frame
        var frameId = 'jUploadFrame' + id;

        if (window.ActiveXObject) {
            //var io = document.createElement('<iframe id="' + frameId + '" name="' + frameId + '" />');
            var io = document.createElement('iframe');
            io.id = frameId;
            io.name = frameId;
            if (typeof uri == 'boolean') {
                io.src = 'javascript:false';
            }
            else if (typeof uri == 'string') {
                io.src = uri;
            }
        }
        else {
            var io = document.createElement('iframe');
            io.id = frameId;
            io.name = frameId;
        }
        io.style.position = 'absolute';
        io.style.top = '-1000px';
        io.style.left = '-1000px';

        document.body.appendChild(io);

        return io
    },
    createUploadForm: function (id, fileElementId) {
        //create form
        var formId = 'jUploadForm' + id;
        var fileId = 'jUploadFile' + id;
        var form = $('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data">'+
        // '<input type="hidden" name="SESSION_TOKEN_KEY" value="'+localStorage.getItem('SESSION_TOKEN_KEY')+'">'+
        '</form>');
        var oldElement = $('#' + fileElementId);
        var newElement = $(oldElement).clone(true);
        $(oldElement).attr('id', fileId);
        $(oldElement).before(newElement);
        $(oldElement).appendTo(form);
        //set attributes
        $(form).css('position', 'absolute');
        $(form).css('top', '-1200px');
        $(form).css('left', '-1200px');
        $(form).appendTo('body');
        return form;
    },
    addOtherRequestsToForm: function (form, data) {
        // add extra parameter
        var originalElement = $('<input type="hidden" name="" value="">');
        for (var key in data) {
            name = key;
            value = data[key];
            var cloneElement = originalElement.clone();
            cloneElement.attr({ 'name': name, 'value': value });
            $(cloneElement).appendTo(form);
        }
        return form;
    },

    ajaxFileUpload: function (s) {
        s = jQuery.extend({}, jQuery.ajaxSettings, s);
        var id = new Date().getTime()
        var form = jQuery.createUploadForm(id, s.fileElementId);
        if (s.data) form = jQuery.addOtherRequestsToForm(form, s.data);
        var io = jQuery.createUploadIframe(id, s.secureuri);
        var frameId = 'jUploadFrame' + id;
        var formId = 'jUploadForm' + id;
        // Watch for a new set of requests
        if (s.global && !jQuery.active++) {
            jQuery.event.trigger("ajaxStart");
        }
        var requestDone = false;
        // Create the request object
        var xml = {}
        if (s.global)
            jQuery.event.trigger("ajaxSend", [xml, s]);
        // Wait for a response to come back
        var uploadCallback = function (isTimeout) {
            var io = document.getElementById(frameId);
            try {
                if (io.contentWindow) {
                    xml.responseText = io.contentWindow.document.body ? io.contentWindow.document.body.innerHTML : null;
                    xml.responseXML = io.contentWindow.document.XMLDocument ? io.contentWindow.document.XMLDocument : io.contentWindow.document;

                } else if (io.contentDocument) {
                    xml.responseText = io.contentDocument.document.body ? io.contentDocument.document.body.innerHTML : null;
                    xml.responseXML = io.contentDocument.document.XMLDocument ? io.contentDocument.document.XMLDocument : io.contentDocument.document;
                }
            } catch (e) {
                jQuery.handleError(s, xml, null, e);
            }
            if (xml || isTimeout == "timeout") {
                requestDone = true;
                var status;
                try {
                    status = isTimeout != "timeout" ? "success" : "error";
                    // Make sure that the request was successful or notmodified
                    if (status != "error") {
                        // process the data (runs the xml through httpData regardless of callback)
                        var data = jQuery.uploadHttpData(xml, s.dataType);
                        // If a local callback was specified, fire it and pass it the data
                        if (s.success)
                            s.success(data, status);

                        // Fire the global callback
                        if (s.global)
                            jQuery.event.trigger("ajaxSuccess", [xml, s]);
                    } else
                        jQuery.handleError(s, xml, status);
                } catch (e) {
                    status = "error";
                    jQuery.handleError(s, xml, status, e);
                }

                // The request was completed
                if (s.global)
                    jQuery.event.trigger("ajaxComplete", [xml, s]);

                // Handle the global AJAX counter
                if (s.global && ! --jQuery.active)
                    jQuery.event.trigger("ajaxStop");

                // Process result
                if (s.complete)
                    s.complete(xml, status);

                jQuery(io).unbind()

                setTimeout(function () {
                    try {
                        $(io).remove();
                        $(form).remove();

                    } catch (e) {
                        jQuery.handleError(s, xml, null, e);
                    }

                }, 100)

                xml = null

            }
        }
        // Timeout checker
        if (s.timeout > 0) {
            setTimeout(function () {
                // Check to see if the request is still happening
                if (!requestDone) uploadCallback("timeout");
            }, s.timeout);
        }
        try {
            // var io = $('#' + frameId);
            var form = $('#' + formId);
            $(form).attr('action', s.url +'?SESSION_TOKEN_KEY=' + localStorage.getItem('SESSION_TOKEN_KEY') );
            $(form).attr('method', 'POST');
            $(form).attr('target', frameId);
            if (form.encoding) {
                form.encoding = 'multipart/form-data';
            }
            else {
                form.enctype = 'multipart/form-data';
            }
            console.log($(form)[0].outerHTML);
            $(form).submit();

        } catch (e) {
            jQuery.handleError(s, xml, null, e);
        }
        if (window.attachEvent) {
            document.getElementById(frameId).attachEvent('onload', uploadCallback);
        }
        else {
            document.getElementById(frameId).addEventListener('load', uploadCallback, false);
        }
        return { abort: function () { } };

    },

    uploadHttpData: function (r, type) {
        var data = !type;
        data = type == "xml" || data ? r.responseXML : r.responseText;
        // If the type is "script", eval it in global context
        if (type == "script")
            jQuery.globalEval(data);
        // Get the JavaScript object, if JSON is used.
        if (type == "json") {
            // If you add mimetype in your response,
            // you have to delete the '<pre></pre>' tag.
            // The pre tag in Chrome has attribute, so have to use regex to remove
            var data = r.responseText;
            var rx = new RegExp("<pre.*?>(.*?)</pre>", "i");
            var am = rx.exec(data);
            //this is the desired data extracted
            var data = (am) ? am[1] : "";    //the only submatch or empty
            eval("data = " + data);
        }
        // evaluate scripts within html
        if (type == "html")
            jQuery("<div>").html(data).evalScripts();
        //alert($('param', data).each(function(){alert($(this).attr('value'));}));
        return data;
    },
    handleError: function (s, xhr, status, e) {
        // If a local callback was specified, fire it    
        if (s.error) {
            s.error.call(s.context || s, xhr, status, e);
        }

        // Fire the global callback    
        if (s.global) {
            (s.context ? jQuery(s.context) : jQuery.event).trigger("ajaxError", [xhr, s, e]);
        }
    }
})

//ajax文件上传，参考userEdit.html
function upload(imgId,refresh) {
    //判断是否有选择上传文件
    var imgPath = $(imgId).val();
    if (imgPath == "") {
        alert("请选择上传的文件！");
        return;
    }
    //判断上传文件的后缀名
    // var strExtension = imgPath.substr(imgPath.lastIndexOf('.') + 1);
    // if (strExtension != 'jpg' && strExtension != 'gif'
    //     && strExtension != 'png' && strExtension != 'bmp') {
    //     alert("请选择图片文件");
    //     return;
    // }
    $.ajaxFileUpload({
        url: '/storage/api/upload', 
        secureuri: false, 
        fileElementId: imgId, //与页面中file相对应的ID值
        //processData : false, 
        //contentType : false,
        dataType: 'json', //返回数据类型:text，xml，json，html,scritp,jsonp五种
        success: function (data, status) {
            console.log(data);
            if(data.code == 200){
                var url = data.data[0].url;
                if(refresh){
                    //及时刷新页面img的显示
                    $('.'+imgId).attr("src", url)
                }
                $('input[name="'+imgId+'"]').val(url);
                hideMask();
                // $('#pageone').create();
            }else{
                showMsg(data.message);
            }
            
        },
        error: function (data, status, e) { 
            console.log(data);
            console.log(e);
        }
    })
}


//获取本地文件框图形地址（img图形预览用）
function getLocalUrl(file) {
    var url = null;
    if (window.createObjectURL != undefined) { // basic
        url = window.createObjectURL(file);
    } else if (window.URL != undefined) { // mozilla(firefox)
        url = window.URL.createObjectURL(file);
    } else if (window.webkitURL != undefined) { // webkit or chrome
        url = window.webkitURL.createObjectURL(file);
    }
    return url;
}

$(function(){
    $("input[type='file']").change(function() {
        // 上传前本地显示预览
        // var objUrl = getLocalUrl(this.files[0]);
        // if (objUrl) {
        //    $('.'+$(this).attr('id')).attr("src", objUrl);
        // }
        // showMask();
        upload($(this).attr('id'),true);
    });
})