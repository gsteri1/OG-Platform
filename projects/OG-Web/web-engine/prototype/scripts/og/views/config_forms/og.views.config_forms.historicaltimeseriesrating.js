/*
 * @copyright 2011 - present by OpenGamma Inc
 * @license See distribution for license
 */
$.register_module({
    name: 'og.views.config_forms.historicaltimeseriesrating',
    dependencies: [
        'og.api',
        'og.common.util.ui'
    ],
    obj: function () {
        var module = this, Form = og.common.util.ui.Form,
            INDX = '<INDEX>';
        return og.views.config_forms['default'].preload({
            type: module.name.split('.').pop(),
            meta: [
                ['0',                                       Form.type.STR],
                [['rules', INDX, 'fieldName'].join('.'),    Form.type.STR],
                [['rules', INDX, 'fieldValue'].join('.'),   Form.type.STR],
                [['rules', INDX, 'rating'].join('.'),       Form.type.BYT]
            ].reduce(function (acc, val) {return acc[val[0]] = val[1], acc;}, {})
        });
    }
});