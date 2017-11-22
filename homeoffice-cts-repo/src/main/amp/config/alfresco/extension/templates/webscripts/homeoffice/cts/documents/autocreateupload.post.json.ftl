<#assign datetimeformat="EEE, dd MMM yyyy HH:mm:ss zzz">
{
    "name" : "${doc.name}",
    "id" : "${doc.id}",
    "type" : "${doc.type.id}",
    "name" : "${doc.name}",
    "createdBy" : "${doc.createdBy}",
    "createdDate" : "${doc.creationDate.time?datetime}",
    "size" : "${(doc.contentStreamLength/1024)?int} KB",
    "MIME type" : "${doc.contentStreamMimeType}"
}