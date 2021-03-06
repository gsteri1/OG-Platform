/*
 * @copyright 2011 - present by OpenGamma Inc
 * @license See distribution for license
 */
$.register_module({
    name: 'og.views.config_forms.currencymatrix',
    dependencies: [
        'og.api',
        'og.common.util.ui'
    ],
    obj: function () {
        var module = this, Form = og.common.util.ui.Form,
            INDX = '<INDEX>',
            CRCV = 'crossConvert', UNIQ = 'uniqueId',
            VLRQ = 'valueReq', VLNM = 'valueName', CPTI = 'computationTargetIdentifier',
            CPTT = 'computationTargetType', RECP = 'reciprocal';
        return og.views.config_forms['default'].preload({
            type: module.name.split('.').pop(),
            meta: [
                [['0', INDX].join('.'),             Form.type.STR],
                [[CRCV, '*', '*'].join('.'),        Form.type.STR],
                [[CRCV, '*', '*', INDX].join('.'),  Form.type.STR],
                [UNIQ,                              Form.type.STR],
                [[VLRQ, '*', '*', VLNM].join('.'),  Form.type.STR],
                [[VLRQ, '*', '*', CPTI].join('.'),  Form.type.STR],
                [[VLRQ, '*', '*', CPTT].join('.'),  Form.type.STR],
                [[VLRQ, '*', '*', RECP].join('.'),  Form.type.BOO]
            ].reduce(function (acc, val) {return acc[val[0]] = val[1], acc;}, {})
        });
    }
});