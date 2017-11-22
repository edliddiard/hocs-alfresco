<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Email Notifications Queue Administration Page</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
          integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
<#--<link rel="stylesheet" href="https://cdn.datatables.net/1.10.11/css/dataTables.bootstrap.min.css" />-->

    <style>
        body {
            padding-top: 70px;
            padding-bottom: 30px;
        }

        .cts-toolbar {
            padding: 0px 15px 15px;
        }
    </style>

    <script type="text/javascript" language="javascript" src="//code.jquery.com/jquery-1.12.0.min.js"/>
<#--<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/1.10.11/js/jquery.dataTables.min.js"></script>-->
<#--<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/1.10.11/js/dataTables.bootstrap.min.js"></script>-->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
            integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
            crossorigin="anonymous"></script>

    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/r/bs-3.3.5/jq-2.1.4,dt-1.10.8/datatables.min.css"/>

    <script type="text/javascript" src="https://cdn.datatables.net/r/bs-3.3.5/jqc-1.11.3,dt-1.10.8/datatables.min.js"></script>
    <script type="text/javascript" charset="utf-8">

        // Details button popover
        $(function () {
            $('[data-toggle="popover"]').popover()
        })

        $(document).ready(function() {
            var table = $('#ctsmail').DataTable({
                        'columnDefs': [{
                            'targets': 0,
                            'searchable': false,
                            'orderable': false,
                            'className': 'dt-body-center'
//                            'render': function (data, type, full, meta) {
//                                return '<input type="checkbox" name="uuid" value="' + $('<div/>').text(data).html() + '">';
//                            }
                        }],
                        'order': [[1, 'asc']],
                        'iDisplayLength': 50,
                        'lengthMenu': [[50, 100, 250, 500, -1], [50, 100, 250, 500, "All"]]
                    }
            );

            $('#ctsmail tbody').on( 'click', 'tr', function () {
                $(this).toggleClass('selected');
            } );


            // Button events
            $('#deleteButton').click( function () {
                $('input[name="uuid"]:checked').each(function(){

                    var id = $(this).val();

                    $.ajax({
                        url: '/alfresco/service/cmis/i/' + id,
                        type: 'DELETE',
                        success: function() {
                            $(this).parent('tr').remove();
                            location.reload();
                        }
                    });


                });
            } );

            $('#deleteAllButton').click( function () {
                $('input[name="uuid"]').each(function(){

                    var id = $(this).val();

                    $.ajax({
                        url: '/alfresco/service/cmis/i/' + id,
                        type: 'DELETE',
                        success: function() {
                            $(this).parent('tr').remove();
                            location.reload();
                        }
                    });


                });
            } );

            // Handle click on "Select all" control
            $('#select-all').on('click', function(){
                // Get all rows with search applied
                var rows = table.rows({ 'search': 'applied' }).nodes();
                // Check/uncheck checkboxes for all rows in the table
                $('input[type="checkbox"]', rows).prop('checked', this.checked);
            });

            // Handle click on checkbox to set state of "Select all" control
            $('#ctsmail tbody').on('change', 'input[type="checkbox"]', function(){
                // If checkbox is not checked
                if(!this.checked){
                    var el = $('#select-all').get(0);
                    // If "Select all" control is checked and has 'indeterminate' property
                    if(el && el.checked && ('indeterminate' in el)){
                        // Set visual state of "Select all" control
                        // as 'indeterminate'
                        el.indeterminate = true;
                    }
                }
            });


            $('#refreshButton').click( function () {
                location.reload();
            });

        } );

    </script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>

<#--<p> Searching in folder: ${folder.displayPath}/${folder.name}</p>-->

<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <span class="navbar-brand">Email Jobs Administration Page </span>
        </div>
    </div>
</nav>

<div class="container-fluid">

    <div class="cts-toolbar btn-toolbar" role="toolbar">
        <button id="deleteButton" type="button" class="btn btn-default"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span> Delete</button>
        <button id="deleteAllButton" type="button" class="btn btn-default"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Delete All</button>
        <button id="resendButton" type="button" class="btn btn-default"><span class="glyphicon glyphicon-transfer" aria-hidden="true"></span> Resend</button>
        <button id="refreshButton" type="button" class="btn btn-default"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span> Refresh</button>
    </div>
</div>

<div class="container-fluid">
    <table id="ctsmail" class="table">
        <thead>
        <tr>
            <th><input type="checkbox" name="select_all" value="1" id="select-all"></th>
            <th>Message</th>
            <th>Created</th>
            <th>Username</th>
            <th>Case URN</th>
            <th>Subject</th>
            <th>Sent</th>
        </tr>
        </thead>
        <tbody>
        <#list folder.children as child>
            <#assign created = "{http://www.alfresco.org/model/content/1.0}created"/>
            <#assign username = "{http://www.alfresco.org/model/content/1.0}userName"/>
            <#assign title = "{http://www.alfresco.org/model/content/1.0}title"/>
            <#assign urn = "{http://cts-beta.homeoffice.gov.uk/model/content/1.0}urnSuffix"/>
            <#assign type = "{http://cts-beta.homeoffice.gov.uk/model/content/1.0}correspondenceType"/>
            <#assign subject = "{http://www.alfresco.org/model/content/1.0}subjectline"/>
            <#assign status = "{http://cts-beta.homeoffice.gov.uk/model/mail/1.0}status"/>
            <#assign error = "{http://cts-beta.homeoffice.gov.uk/model/mail/1.0}error"/>
            <#assign failureCount = "{http://cts-beta.homeoffice.gov.uk/model/mail/1.0}failureCount"/>
            <#assign sentDate = "{http://www.alfresco.org/model/content/1.0}sentdate"/>
            <#assign addressee = "{http://www.alfresco.org/model/content/1.0}addressee"/>

        <tr <#if child.properties[status]?? && child.properties[status] == "sent"> class="success"
        <#elseif child.properties[status]?? && child.properties[status] == "fail"> class="danger"
        <#elseif child.properties[status]?? && child.properties[status] == "retrying"> class="warning"
        <#elseif child.properties[status]?? && child.properties[status] == "new"> class="info"
        <#elseif child.properties[status]?? && child.properties[status] == "sending"> class="active"
        </#if>>
            <th><input name="uuid" type="checkbox" value="${child.id}"></th>
            <td>${child.properties[title]}-${child.name}</td>
            <td>
                <#if child.properties[created]?exists>
          ${child.properties[created]?datetime}
        </#if>
            </td>
            <td>
                <#if child.properties[username]?exists>
          ${child.properties[username]}
        </#if>
            </td>
            <td>
                <#if child.properties[type]?exists>
                ${child.properties[type]}</#if>/<#if child.properties[urn]?exists>${child.properties[urn]}
                </#if>
            </td>
            <td>
                <#if child.properties[subject]?exists>
          ${child.properties[subject]}
        </#if>
            </td>
            <td>
                <#if child.properties[status]?? && child.properties[status] == "sent">
                    <span class="alert-success glyphicon glyphicon-ok-sign" aria-hidden="true"></span> Ok
                <#elseif child.properties[status]?? && child.properties[status] == "fail">
                    <span class="alert-danger glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> Fail
                    <#if child.properties[error]?exists>
                        <button type="button" class="btn btn-xs btn-danger" data-toggle="popover" data-placement="left"
                                title="${child.properties[status]}"
                                data-content="${child.properties[error]}">
                            Details
                        </button>
                    </#if>
                <#elseif child.properties[status]?? && child.properties[status] == "retrying">
                    <span class="alert-warning glyphicon glyphicon-repeat" aria-hidden="true"></span> Retrying
                    <#if child.properties[error]?exists>
                        <button type="button" class="btn btn-xs btn-warning" data-toggle="popover" data-placement="left"
                                title="${child.properties[status]} ${child.properties[failureCount]!} times"
                                data-content="${child.properties[error]!}">
                            Details
                        </button>
                    </#if>
                <#else>
                    <span class="alert-info glyphicon glyphicon-time" aria-hidden="true"></span>
                </#if>
            </td>
        </tr>
        </#list>
        </tbody>
    </table>
</div>


</body>
</html>