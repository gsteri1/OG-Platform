/**
 * view for securities section
 */
$.register_module({
    name: 'og.views.timeseries',
    dependencies: [
        'og.common.routes',
        'og.common.masthead.menu',
        'og.common.search_results.core',
        'og.common.util.ui.message',
        'og.views.common.layout',
        'og.common.util.ui.toolbar'
    ],
    obj: function () {
        var api = og.api.rest, routes = og.common.routes, module = this, timeseries,
            masthead = og.common.masthead, search = og.common.search_results.core(), details = og.common.details,
            ui = og.common.util.ui, layout = og.views.common.layout,
            page_name = 'timeseries',
            check_state = og.views.common.state.check.partial('/' + page_name),
            formatter = og.common.slickgrid.formatters.timeseries,
            search_options = {
                'selector': '.og-js-results-slick', 'page_type': 'timeseries',
                'columns': [
                    {
                        id: 'datasource', name: 'Datasource', field: 'datasource',
                        width: 90, cssClass: 'og-uppercase',
                        filter_type: 'input'
                    },
                    {
                        id: 'BLOOMBERG_TICKER', name: 'Bloomberg Ticker', field: 'BLOOMBERG_TICKER',
                        width: 150, cssClass: 'og-link',
                        formatter: formatter,
                        filter_type: 'input'
                    },
                    {
                        id: 'BLOOMBERG_BUID', name: 'Bloomberg BUID',  field: 'BLOOMBERG_BUID',
                        width: 150, cssClass: 'og-link',
                        formatter: formatter,
                        filter_type: 'input'
                    },
                    {
                        id: 'CUSIP', name: 'CUSIP', field: 'CUSIP',
                        width: 70, cssClass: 'og-link',
                        formatter: formatter,
                        filter_type: 'input'
                    },
                    {
                        id: 'ISIN', name: 'CUSIP', field: 'CUSIP',
                        width: 70, cssClass: 'og-link',
                        formatter: formatter,
                        filter_type: 'input'
                    },
                    {
                        id: 'RIC', name: 'RIC', field: 'RIC',
                        width: 70, cssClass: 'og-link',
                        formatter: formatter,
                        filter_type: 'input'
                    },
                    {
                        id: 'SEDOL1', name: 'SEDOL1', field: 'SEDOL1',
                        width: 70, cssClass: 'og-link',
                        formatter: formatter,
                        filter_type: 'input'
                    }
                ]},
            buttons = {
                'new': function () {
                    ui.dialog({
                        type: 'input',
                        title: 'Add New Timeseries',
                        fields: [
                            {type: 'select', name: 'Scheme Type', id: 'scheme',
                                    options: [
                                        {name: 'Bloomberg Ticker', value: 'BLOOMBERG_TICKER'},
                                        {name: 'Bloomberg Ticker/Coupon/Maturity', value: 'BLOOMBERG_TCM'},
                                        {name: 'Bloomberg BUID', value: 'BLOOMBERG_BUID'},
                                        {name: 'CUSIP', value: 'CUSIP'},
                                        {name: 'ISIN', value: 'ISIN'},
                                        {name: 'RIC', value: 'RIC'},
                                        {name: 'SEDOL', value: 'CSEDOL1'}
                                    ]
                            },
                            {type: 'input', name: 'Data provider', id: 'provider'},
                            {type: 'input', name: 'Data field', id: 'field'},
                            {type: 'input', name: 'Start date(yyyy-mm-dd)', id: 'start'},
                            {type: 'input', name: 'End date(yyyy-mm-dd)', id: 'end'},
                            {type: 'textarea', name: 'Identifiers', id: 'identifiers'}
                        ],
                        buttons: {
                            'Ok': function () {
                                $(this).dialog('close');
                                api.timeseries.put({
                                    handler: function (r) {
                                        window.location.hash = routes.hash(
                                                og.views[page_name].rules['load_' + page_name],
                                                        {id: r.meta.id, 'new': true}
                                        )
                                    },
                                    name: ui.dialog({return_field_value: 'name'}) // TODO
                                });
                            }
                        }
                    })
                },
                'delete': function () {
                    ui.dialog({
                        type: 'confirm',
                        title: 'Delete timeseries?',
                        message: 'Are you sure you want to permanently delete this timeseries?',
                        buttons: {
                            'Delete': function () {
                                $(this).dialog('close');
                                api.timeseries.del({
                                    handler: function () {
                                        window.location.hash = routes.hash(og.views[page_name].rules['load'], {});
                                    }, id: args.id
                                });
                            }
                        }
                    })
                }
            },
            default_toolbar_options = {
                buttons: [
                    {name: 'new', handler: buttons['new']},
                    {name: 'up', enabled: 'OG-disabled'},
                    {name: 'edit', enabled: 'OG-disabled'},
                    {name: 'delete', enabled: 'OG-disabled'},
                    {name: 'favorites', enabled: 'OG-disabled'}
                ],
                location: '.OG-toolbar .og-js-buttons'
            },
            active_toolbar_options = {
                buttons: [
                    {name: 'new', handler: buttons['new']},
                    {name: 'up', handler: 'handler'},
                    {name: 'edit', handler: 'handler'},
                    {name: 'delete', handler: buttons['delete']},
                    {name: 'favorites', handler: 'handler'}
                ],
                location: '.OG-toolbar .og-js-buttons'
            };
        module.rules = {
            load: {route: '/' + page_name, method: module.name + '.load'},
            load_filter: {
                route: '/' + page_name
                           + '/filter:/:id?/identifier:?/dataSource:?/dataProvider:?/dataField:?/observationTime:?',
                method: module.name + '.load_filter'
            },
            load_timeseries: {route: '/' + page_name + '/:id', method: module.name + '.load_' + page_name}
        };
        return timeseries = {
            load: function (args) {
                masthead.menu.set_tab(page_name);
                layout('default');
                ui.toolbar(default_toolbar_options);
                search.load($.extend(search_options, {url: args}));
                $('#OG-details .og-main').html('default ' + page_name + ' page');
            },
            load_filter: function (args) {
                check_state({args: args, conditions: [
                    {new_page: function () {
                        state = {filter: true};
                        timeseries.load(args);
                        args.id
                            ? routes.go(routes.hash(module.rules.load_timeseries, args))
                            : routes.go(routes.hash(module.rules.load, args));
                    }}
                ]});
                delete args['filter'];
                search.filter($.extend(args, {filter: true}));
            },
            load_timeseries: function (args) {
                // Load search if changed
                if (routes.last()) {
                    if (routes.last().page !== module.rules.load.route) {
                        masthead.menu.set_tab(page_name);
                        ui.toolbar(active_toolbar_options);
                        search.load($.extend(search_options, {url: args}));
                    } else ui.toolbar(active_toolbar_options);
                } else {
                    masthead.menu.set_tab(page_name);
                    ui.toolbar(active_toolbar_options);
                    search.load($.extend(search_options, {url: args}));
                }
                // Setup details page
                layout('default');
                api.timeseries.get({
                    handler: function (result) {
                        if (result.error) return alert(result.message);
                        var json = result.data, f = details.timeseries_functions;
                        og.api.text({module: module.name, handler: function (template) {
                            var stop_loading = ui.message.partial({location: '#OG-details', destroy: true});
                            $.tmpl(template, json.templateData).appendTo($('#OG-details .og-main').empty());
                            f.render_timeseries_identifiers('.OG-timeseries .og-js-identifiers', json.identifiers);
                            ui.render_plot('.OG-timeseries .og-js-timeseriesPlot', json.timeseries.data);
                            f.render_timeseries_table('.OG-timeseries .og-js-table', {
                                'fieldLabels': json.timeseries.fieldLabels,
                                'data': json.timeseries.data
                            }, stop_loading);
                            // Hook up CSV button
                            $('.OG-timeseries .og-js-timeSeriesCsv').click(function () {
                                window.location.href = 'http://localhost:8080/jax/timeseries/Tss::3535.csv';
                            });
                            ui.expand_height_to_window_bottom({element: '.OG-timeseries .og-dataPoints tbody'});
                            ui.expand_height_to_window_bottom({element: '.OG-timeseries .og-dataPoints table'});
                            details.favorites();
                        }});
                    },
                    id: args.id,
                    loading: function () {
                        ui.message({location: '#OG-details', message: {0: 'loading...', 3000: 'still loading...'}});
                    }
                });
            },
            init: function () {
                for (var rule in module.rules) routes.add(module.rules[rule]);
            },
            rules: module.rules
        };
    }
});