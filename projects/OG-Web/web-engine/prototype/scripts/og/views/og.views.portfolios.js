/*
 * @copyright 2009 - present by OpenGamma Inc
 * @license See distribution for license
 */
$.register_module({
    name: 'og.views.portfolios',
    dependencies: [
        'og.api.rest',
        'og.api.text',
        'og.common.masthead.menu',
        'og.common.routes',
        'og.common.search_results.core',
        'og.common.util.history',
        'og.common.util.ui.dialog',
        'og.common.util.ui.message',
        'og.common.util.ui.toolbar',
        'og.views.common.layout',
        'og.views.common.versions',
        'og.views.common.state',
        'og.views.common.default_details'
    ],
    obj: function () {
        var api = og.api,
            common = og.common,
            details = common.details,
            history = common.util.history,
            masthead = common.masthead,
            routes = common.routes,
            search,
            ui = common.util.ui,
            module = this,
            page_name = module.name.split('.').pop(),
            check_state = og.views.common.state.check.partial('/' + page_name),
            json = {},
            portfolios,
            toolbar_buttons = {
                'new': function () {
                    ui.dialog({
                        type: 'input',
                        title: 'Add New Portfolio',
                        fields: [{type: 'input', name: 'Portfolio Name', id: 'name'}],
                        buttons: {
                            'OK': function () {
                                $(this).dialog('close');
                                api.rest.portfolios.put({
                                    handler: function (r) {
                                        if (r.error) return ui.dialog({type: 'error', message: r.message});
                                        routes.go(routes.hash(module.rules.load_new_portfolios,
                                                $.extend({}, routes.last().args, {id: r.meta.id, 'new': true})
                                        ));
                                    },
                                    name: ui.dialog({return_field_value: 'name'})
                                });
                            }
                        }
                    })
                },
                'delete': function () {
                    ui.dialog({
                        type: 'confirm',
                        title: 'Delete portfolio?',
                        message: 'Are you sure you want to permanently delete this portfolio?',
                        buttons: {
                            'Delete': function () {
                                var obj = {
                                    id: routes.last().args.id,
                                    handler: function (r) {
                                        var last = routes.last(), args_obj = {};
                                        if (r.error) return ui.dialog({type: 'error', message: r.message});
                                        if (json.template_data.parent_node_id) {
                                            args_obj.node = json.template_data.parent_node_id;
                                            args_obj.id = json.template_data.object_id;
                                        }
                                        portfolios.deleted = true;
                                        routes.handler();
                                    }
                                };
                                if (routes.last().args.node) obj.node = routes.last().args.node;
                                $(this).dialog('close');
                                api.rest.portfolios.del(obj);
                            }
                        }
                    })
                }
            },
            options = {
                slickgrid: {
                    'selector': '.OG-js-search', 'page_type': page_name,
                    'columns': [
                        {
                            id: 'name', field: 'name', width: 300, cssClass: 'og-link', toolTip: 'name',
                            name: '<input type="text" placeholder="Name" '
                                + 'class="og-js-name-filter" style="width: 280px;">'
                        }
                    ]
                },
                toolbar: {
                    'default': {
                        buttons: [
                            {name: 'delete', enabled: 'OG-disabled'},
                            {name: 'new', handler: toolbar_buttons['new']}
                        ],
                        location: '.OG-toolbar'
                    },
                    active: {
                        buttons: [
                            {name: 'delete', handler: toolbar_buttons['delete']},
                            {name: 'new', handler: toolbar_buttons['new']}
                        ],
                        location: '.OG-toolbar'
                    }
                }
            },
            default_details = og.views.common.default_details.partial(page_name, 'Portfolios', options),
            details_page = function (args) {
                var hook_up_add_portfolio_form = function () {
                        var do_update = function () {
                            api.rest.portfolios.put({
                                handler: function (r) {
                                    if (r.error) {ui.dialog({type: 'error', message: r.message}); return}
                                    routes.go(routes.hash(module.rules.load_new_portfolios,
                                            $.extend({},routes.current().args, {'new': true})
                                    ));
                                },
                                name: ui.dialog({return_field_value: 'name'}),
                                id: json.template_data.object_id,
                                node: json.template_data.node,
                                'new': true
                            });
                        };
                        $('.OG-js-add-sub-portfolio').click(function () {
                            ui.dialog({
                                type: 'input',
                                title: 'Add New sub Portfolio',
                                fields: [{type: 'input', name: 'Portfolio Name', id: 'name'}],
                                buttons: {
                                    'OK': function () {
                                        if (ui.dialog({return_field_value: 'name'}) === '') return;
                                        $(this).dialog('close');
                                        do_update();
                                    }
                                }
                            });
                            return false;
                        });
                    },
                    hook_up_add_position_form = function () {
                        var do_update = function (e, id) {
                            api.rest.portfolios.put({
                                handler: function (r) {
                                    if (r.error) return ui.dialog({type: 'error', message: r.message});
                                    // TODO: prevent search from reloading
                                    routes.go(routes.hash(module.rules.load_new_portfolios,
                                         $.extend({}, routes.last().args,
                                             {id: json.template_data.object_id, 'new': true})
                                    ));
                                },
                                position: id ? id.item.value : $input.val(),
                                id: json.template_data.object_id,
                                node: json.template_data.node
                           });
                           ui.dialog({type: 'input', action: 'close'});
                        };
                        $('.OG-js-add-position').click(function () {
                            ui.dialog({
                                type: 'input',
                                title: 'Add Position',
                                fields: [{type: 'input', name: 'Identifier', id: 'name'}],
                                buttons: {
                                    'OK': function () {
                                        if (ui.dialog({return_field_value: 'name'}) === '') return;
                                        do_update();
                                        $(this).dialog('close');
                                    }
                                }
                            });
                            $('#og-js-dialog-name').autocomplete({
                                source: function (obj, callback) {
                                    api.rest.positions.get({
                                        handler: function (r) {
                                            callback(
                                                r.data.data.map(function (val) {
                                                    var arr = val.split('|');
                                                    return {value: arr[0], label: arr[1], id: arr[0], node: arr[1]};
                                                })
                                            );
                                        },
                                        loading: '', page_size: 10, page: 1,
                                        identifier: '*' + obj.term.replace(/\s/g, '*') + '*'
                                    });
                                },
                                minLength: 1,
                                select: function (e, ui) {do_update(e, ui);}
                            });
                            return false;
                        });
                    },
                    render_portfolio_rows = function (selector, json) {
                        var display_columns = [], data_columns = [], format = common.slickgrid.formatters.portfolios;
                        if (json.portfolios[0]) {
                            display_columns = [{
                                id: 'name', name: 'Name', field: 'name', cssClass: 'og-link',
                                width: 300, formatter: format
                            }],
                            data_columns = [{
                                id: 'id', name: 'Id', field: 'id', width: 100, formatter: format
                            }];
                        } else {
                            display_columns = [{id: 'name', name: 'Name', field: 'name', width: 300}],
                            json.portfolios = [{name: 'No portfolios', id: ''}]
                        }
                        slick = new Slick.Grid(selector, json.portfolios, display_columns.concat(data_columns));
                        slick.setColumns(display_columns);
                        slick.onClick.subscribe(function (e, dd) {
                            var rule = module.rules.load_portfolios,
                                node = json.portfolios[dd.row].id,
                                href = routes.hash(rule, {id: routes.current().args.id, node: node});
                            if ($(e.target).hasClass('og-icon-delete')) {
                                ui.dialog({
                                    type: 'confirm',
                                    title: 'Delete sub portfolio?',
                                    message: 'Are you sure you want to permanently delete this sub portfolio?',
                                    buttons: {'Delete': function () {
                                        api.rest.portfolios.del({
                                            id: routes.last().args.id, node: node,
                                            handler: function (r) {
                                                if (r.error) return ui.dialog({type: 'error', message: r.message});
                                                portfolios.deleted = true;
                                                routes.handler();
                                            }
                                        });
                                        $(this).dialog('close');
                                    }}
                                });
                            } else routes.go(href);
                        });
                        slick.onMouseEnter.subscribe(function (e) {
                           $(e.currentTarget).closest('.slick-row').find('.og-button').show();
                        });
                        slick.onMouseLeave.subscribe(function (e) {
                           $(e.currentTarget).closest('.slick-row').find('.og-button').hide();
                        });
                    },
                    render_position_rows = function (selector, json) {
                        var display_columns = [], data_columns = [], format = common.slickgrid.formatters.positions;
                        if (json.positions[0]) {
                            display_columns = [
                                {id:"name", name:"Name", field:"name", width: 300, cssClass: 'og-link'},
                                {id:"quantity", name:"Quantity", field:"quantity", width: 80, formatter: format}
                            ],
                            data_columns = [
                                {id:"id", name:"Id", field:"id", width: 100, formatter: format}
                            ];
                        } else {
                            display_columns = [
                                {id:"name", name:"Name", field:"name", width: 300},
                                {id:"quantity", name:"Quantity", field:"quantity", width: 80}
                            ],
                            json.positions = [{name: 'No positions', quantity:'', id: ''}]
                        }
                        slick = new Slick.Grid(selector, json.positions, display_columns.concat(data_columns));
                        slick.setColumns(display_columns);
                        slick.onClick.subscribe(function (e, dd) {
                            var rule = og.views.positions.rules['load_positions'], row = json.positions[dd.row],
                                position = row.id, position_name = row.name,
                                href = routes.hash(rule, {id: position});
                            if ($(e.target).hasClass('og-icon-unhook')) {
                                ui.dialog({
                                    type: 'confirm',
                                    title: 'Remove Position?',
                                    message: 'Are you sure you want to remove the position '
                                        + '<strong style="white-space: nowrap">' + position_name + '</strong>'
                                        + ' from this portfolio?',
                                    buttons: {'Delete': function () {
                                        api.rest.portfolios.del({
                                            id: routes.last().args.id,
                                            node: json.template_data.node,
                                            position: position,
                                            handler: function (r) {
                                                if (r.error) return ui.dialog({type: 'error', message: r.message});
                                                routes.handler();
                                            }
                                        });
                                        $(this).dialog('close');
                                    }}
                                });
                            } else routes.go(href);

                        });
                        slick.onMouseEnter.subscribe(function (e) {
                           $(e.currentTarget).closest('.slick-row').find('.og-button').show();
                        });
                        slick.onMouseLeave.subscribe(function (e) {
                           $(e.currentTarget).closest('.slick-row').find('.og-button').hide();
                        });
                    };
                api.rest.portfolios.get({
                    handler: function (result) {
                        if (result.error) return alert(result.message); // TODO: replace with UI error dialog
                        json = result.data;
                        history.put({
                            name: json.template_data.name,
                            item: 'history.portfolios.recent',
                            value: routes.current().hash
                        });
                        og.api.text({module: module.name, handler: function (template) {
                            var error_html = '\
                                    <section class="OG-box og-box-glass og-box-error OG-shadow-light">\
                                        This portfolio has been deleted\
                                    </section>\
                                ',
                                $html = $.tmpl(template, json.template_data),
                                layout = og.views.common.layout, header, content;
                            header = $.outer($html.find('> header')[0]);
                            content = $.outer($html.find('> section')[0]);
                            $('.ui-layout-inner-center .ui-layout-header').html(header);
                            $('.ui-layout-inner-center .ui-layout-content').html(content);
                            ui.message({location: '.ui-layout-inner-center', destroy: true});
                            ui.toolbar(options.toolbar.active);
                            if (json.template_data && json.template_data.deleted) {
                                $('.ui-layout-inner-north').html(error_html);
                                layout.inner.sizePane('north', '0');
                                layout.inner.open('north');
                                $('.OG-toolbar .og-js-delete').addClass('OG-disabled').unbind();
                            } else {
                                layout.inner.close('north');
                                $('.ui-layout-inner-north').empty();
                            }
                            hook_up_add_portfolio_form(), hook_up_add_position_form();
                            render_portfolio_rows('.OG-js-details-panel .og-js-portfolios', json);
                            render_position_rows('.OG-js-details-panel .og-js-positions', json);
                            ui.content_editable({
                                attribute: 'data-og-editable',
                                handler: function () {
                                    routes.go(routes.hash(module.rules.load_edit_portfolios, $.extend(args, {
                                        edit: 'true'
                                    })));
                                }
                            });
                            layout.inner.resizeAll();
                        }});
                    },
                    id: args.id,
                    node: args.node,
                    version: args.version,
                    loading: function () {
                        if (!og.views.common.layout.inner.state.south.isClosed) {og.views.common.versions.load()}
                        ui.message({
                            location: '.ui-layout-inner-center',
                            message: {0: 'loading...', 3000: 'still loading...'}
                        });
                    }
                });
            },
            load_portfolios_without = function (field, args) {
                check_state({args: args, conditions: [{new_page: portfolios.load}]});
                delete args[field];
                portfolios.search(args);
                routes.go(routes.hash(module.rules.load_portfolios, args));
            };
        module.rules = {
            load: {route: '/' + page_name + '/name:?', method: module.name + '.load'},
            load_filter: {
                route: '/' + page_name + '/filter:/:id?/name:?', method: module.name + '.load_filter'
            },
            load_portfolios: {
                route: '/' + page_name + '/:id/:node?/version:?/name:?', method: module.name + '.load_' + page_name
            },
            load_new_portfolios: {
                route: '/' + page_name + '/:id/:node?/new:/name:?', method: module.name + '.load_new_' + page_name
            },
            load_edit_portfolios: {
                route: '/' + page_name + '/:id/:node?/edit:/name:?', method: module.name + '.load_edit_' + page_name
            }
        };
        return portfolios = {
            load: function (args) {
                check_state({args: args, conditions: [
                    {new_page: function (args) {
                        portfolios.search(args);
                        masthead.menu.set_tab(page_name);
                    }}
                ]});
                if (args.id) return;
                default_details();
            },
            load_filter: function (args) {
                check_state({args: args, conditions: [
                    {new_page: function () {
                        state = {filter: true};
                        portfolios.load(args);
                        args.id
                            ? routes.go(routes.hash(module.rules.load_portfolios, args))
                            : routes.go(routes.hash(module.rules.load, args));
                    }}
                ]});
                delete args['filter'];
                search.filter($.extend(args, {filter: true}));
            },
            load_delete: function (args) {
                portfolios.deleted = false;
                portfolios.search(args);
                portfolios.details($.extend({name: args.name}, args));
            },
            load_new_portfolios: load_portfolios_without.partial('new'),
            load_edit_portfolios: load_portfolios_without.partial('edit'),
            load_portfolios: function (args) {
                if (portfolios.deleted) return portfolios.load_delete(args);
                check_state({args: args, conditions: [{new_page: portfolios.load}]});
                portfolios.details(args);
            },
            search: function (args) {
                if (!search) search = common.search_results.core();
                search.load($.extend(options.slickgrid, {url: args}));
            },
            details: details_page,
            init: function () {for (var rule in module.rules) routes.add(module.rules[rule]);},
            rules: module.rules
        };
    }
});