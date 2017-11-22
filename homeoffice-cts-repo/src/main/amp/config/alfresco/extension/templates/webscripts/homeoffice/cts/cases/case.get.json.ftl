<#import "/homeoffice/lib/cts.lib.json.ftl" as ctsLib>
<#assign datetimeformat="dd-MM-yyyy HH:mm:ss">
{
    "folderName": ${ctsCase.name},
    "id" : "${ctsCase.id}",
    "dateCreated" : "${ctsCase.createdAt?datetime?iso("UTC")}",
    <#if ctsCase.autoCreatedCase??>
        "autoCreatedCase" : ${ctsCase.autoCreatedCase?string("true", "false")},
    <#else>
        "autoCreatedCase" : "false",
    </#if>
    "correspondenceType" : ${ctsCase.correspondenceType},
    "caseStatus" : ${ctsCase.caseStatus},
    "caseTask" : ${ctsCase.caseTask},
    "statusUpdatedDatetime" : "${ctsCase.statusUpdatedDatetime?datetime?iso("UTC")}",
    "taskUpdatedDatetime" : "${ctsCase.taskUpdatedDatetime?datetime?iso("UTC")}",
    "ownerUpdatedDatetime" : "${ctsCase.ownerUpdatedDatetime?datetime?iso("UTC")}",
    <#if ctsCase.caseRef??>
        "caseRef" : ${ctsCase.caseRef},
    </#if>
    <#if ctsCase.caseWorkflowStatus??>
        "caseWorkflowStatus" : ${ctsCase.caseWorkflowStatus},
    </#if>
    <#if ctsCase.caseMandatoryFields??>
        "caseMandatoryFields" : ${ctsCase.caseMandatoryFields},
    </#if>
    <#if ctsCase.caseMandatoryFieldDependencies??>
        "caseMandatoryFieldDependencies" : ${ctsCase.caseMandatoryFieldDependencies},
    </#if>
    <#if ctsCase.caseMandatoryFieldStatus??>
        "caseMandatoryFieldStatus" : ${ctsCase.caseMandatoryFieldStatus},
    </#if>
    <#if ctsCase.caseMandatoryFieldTask??>
            "caseMandatoryFieldTask" : ${ctsCase.caseMandatoryFieldTask},
        </#if>
    <#if ctsCase.ogdName??>
        "ogdName" : ${ctsCase.ogdName},
    </#if>
    <#if ctsCase.markupDecision??>
        "markupDecision" : ${ctsCase.markupDecision},
    </#if>
    <#if ctsCase.markupUnit??>
        "markupUnit" : ${ctsCase.markupUnit},
    </#if>
    <#if ctsCase.markupTopic??>
        "markupTopic" : ${ctsCase.markupTopic},
    </#if>
    <#if ctsCase.markupMinister??>
        "markupMinister" : ${ctsCase.markupMinister},
    </#if>
    <#if ctsCase.secondaryTopic??>
        "secondaryTopic" : ${ctsCase.secondaryTopic},
    </#if>
    <#if ctsCase.assignedUnit??>
        "assignedUnit" : ${ctsCase.assignedUnit},
    </#if>
    <#if ctsCase.assignedTeam??>
        "assignedTeam" : ${ctsCase.assignedTeam},
    </#if>
    <#if ctsCase.assignedUser??>
        "assignedUser" : ${ctsCase.assignedUser},
    </#if>
    <#if ctsCase.dateReceived??>
        "dateReceived" : "${ctsCase.dateReceived?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.dateOfLetter??>
        "dateOfLetter" : "${ctsCase.dateOfLetter?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.channel??>
        "channel" : ${ctsCase.channel},
    </#if>
    <#if ctsCase.priority??>
        "priority" : ${ctsCase.priority?string("true", "false")},
    </#if>
    <#if ctsCase.advice??>
        "advice" : ${ctsCase.advice?string("true", "false")},
    </#if>
    <#if ctsCase.caseResponseDeadline??>
        "caseResponseDeadline" : "${ctsCase.caseResponseDeadline?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.poTarget??>
        "poTarget" : "${ctsCase.poTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.cabinetOfficeTarget??>
        "cabinetOfficeTarget" : "${ctsCase.cabinetOfficeTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.dispatchTarget??>
        "dispatchTarget" : "${ctsCase.dispatchTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.unitTarget??>
        "unitTarget" : "${ctsCase.unitTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.homeSecretaryReply??>
        "homeSecretaryReply" : ${ctsCase.homeSecretaryReply?string("true", "false")},
    </#if>
    <#if ctsCase.mpRef??>
        "mpRef" : ${ctsCase.mpRef},
    </#if>
    <#if ctsCase.replyToName??>
        "replyToName" : ${ctsCase.replyToName},
    </#if>
    <#if ctsCase.replyToPostcode??>
        "replyToPostcode" : ${ctsCase.replyToPostcode},
    </#if>
    <#if ctsCase.replyToAddressLine1??>
        "replyToAddressLine1" : ${ctsCase.replyToAddressLine1},
    </#if>
    <#if ctsCase.replyToAddressLine2??>
        "replyToAddressLine2" : ${ctsCase.replyToAddressLine2},
    </#if>
    <#if ctsCase.replyToAddressLine3??>
        "replyToAddressLine3" : ${ctsCase.replyToAddressLine3},
    </#if>
    <#if ctsCase.replyToCountry??>
        "replyToCountry" : ${ctsCase.replyToCountry},
    </#if>
    <#if ctsCase.replyToTelephone??>
        "replyToTelephone" : ${ctsCase.replyToTelephone},
    </#if>
    <#if ctsCase.replyToEmail??>
        "replyToEmail" : ${ctsCase.replyToEmail},
    </#if>
    <#if ctsCase.replyToNumberTenCopy??>
        "replyToNumberTenCopy" : ${ctsCase.replyToNumberTenCopy?string("true", "false")},
    </#if>
    <#if ctsCase.correspondentTitle??>
        "correspondentTitle" : ${ctsCase.correspondentTitle},
    </#if>
    <#if ctsCase.correspondentForename??>
        "correspondentForename" : ${ctsCase.correspondentForename},
    </#if>
    <#if ctsCase.correspondentSurname??>
        "correspondentSurname" : ${ctsCase.correspondentSurname},
    </#if>
    <#if ctsCase.correspondentPostcode??>
        "correspondentPostcode" : ${ctsCase.correspondentPostcode},
    </#if>
    <#if ctsCase.correspondentAddressLine1??>
        "correspondentAddressLine1" : ${ctsCase.correspondentAddressLine1},
    </#if>
    <#if ctsCase.correspondentAddressLine2??>
        "correspondentAddressLine2" : ${ctsCase.correspondentAddressLine2},
    </#if>
    <#if ctsCase.correspondentAddressLine3??>
        "correspondentAddressLine3" : ${ctsCase.correspondentAddressLine3},
    </#if>
    <#if ctsCase.correspondentCountry??>
        "correspondentCountry" : ${ctsCase.correspondentCountry},
    </#if>
    <#if ctsCase.correspondentTelephone??>
        "correspondentTelephone" : ${ctsCase.correspondentTelephone},
    </#if>
    <#if ctsCase.correspondentEmail??>
        "correspondentEmail" : ${ctsCase.correspondentEmail},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentTitle??>
            "thirdPartyCorrespondentTitle" : ${ctsCase.thirdPartyCorrespondentTitle},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentForename??>
        "thirdPartyCorrespondentForename" : ${ctsCase.thirdPartyCorrespondentForename},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentSurname??>
        "thirdPartyCorrespondentSurname" : ${ctsCase.thirdPartyCorrespondentSurname},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentOrganisation??>
            "thirdPartyCorrespondentOrganisation" : ${ctsCase.thirdPartyCorrespondentOrganisation},
        </#if>
    <#if ctsCase.thirdPartyCorrespondentPostcode??>
        "thirdPartyCorrespondentPostcode" : ${ctsCase.thirdPartyCorrespondentPostcode},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentAddressLine1??>
        "thirdPartyCorrespondentAddressLine1" : ${ctsCase.thirdPartyCorrespondentAddressLine1},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentAddressLine2??>
        "thirdPartyCorrespondentAddressLine2" : ${ctsCase.thirdPartyCorrespondentAddressLine2},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentAddressLine3??>
        "thirdPartyCorrespondentAddressLine3" : ${ctsCase.thirdPartyCorrespondentAddressLine3},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentCountry??>
        "thirdPartyCorrespondentCountry" : ${ctsCase.thirdPartyCorrespondentCountry},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentTelephone??>
        "thirdPartyCorrespondentTelephone" : ${ctsCase.thirdPartyCorrespondentTelephone},
    </#if>
    <#if ctsCase.thirdPartyCorrespondentEmail??>
        "thirdPartyCorrespondentEmail" : ${ctsCase.thirdPartyCorrespondentEmail},
    </#if>
    <#if ctsCase.uin??>
        "uin" : ${ctsCase.uin},
    </#if>
    <#if ctsCase.politicianDeadline??>
        "politicianDeadline" : "${ctsCase.politicianDeadline?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.opDate??>
        "opDate" : "${ctsCase.opDate?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.woDate??>
        "woDate" : "${ctsCase.woDate?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.questionNumber??>
        "questionNumber" : ${ctsCase.questionNumber},
    </#if>
    <#if ctsCase.questionText??>
        "questionText" : ${ctsCase.questionText},
    </#if>
    <#if ctsCase.answerText??>
        "answerText" : ${ctsCase.answerText},
    </#if>
    <#if ctsCase.receivedType??>
        "receivedType" : ${ctsCase.receivedType},
    </#if>
    <#if ctsCase.draftDate??>
        "draftDate" : "${ctsCase.draftDate?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.member??>
        "member" : ${ctsCase.member},
    </#if>
    <#if ctsCase.constituency??>
        "constituency" : ${ctsCase.constituency},
    </#if>
    <#if ctsCase.party??>
        "party" : ${ctsCase.party},
    </#if>
    <#if ctsCase.signedByHomeSec??>
        "signedByHomeSec" : ${ctsCase.signedByHomeSec?string("true", "false")},
    </#if>
    <#if ctsCase.signedByLordsMinister??>
        "signedByLordsMinister" : ${ctsCase.signedByLordsMinister?string("true", "false")},
    </#if>
    <#if ctsCase.lordsMinister??>
            "lordsMinister" : ${ctsCase.lordsMinister},
        </#if>
    <#if ctsCase.reviewedByPermSec??>
        "reviewedByPermSec" : ${ctsCase.reviewedByPermSec?string("true", "false")},
    </#if>
    <#if ctsCase.reviewedBySpads??>
        "reviewedBySpads" : ${ctsCase.reviewedBySpads?string("true", "false")},
    </#if>
    <#if ctsCase.roundRobin??>
        "roundRobin" : ${ctsCase.roundRobin?string("true", "false")},
    </#if>
    <#if ctsCase.cabinetOfficeGuidance??>
        "cabinetOfficeGuidance" : ${ctsCase.cabinetOfficeGuidance},
    </#if>
    <#if ctsCase.transferDepartmentName??>
        "transferDepartmentName" : ${ctsCase.transferDepartmentName},
    </#if>
    <#if ctsCase.isGroupedSlave??>
        "isGroupedSlave" : ${ctsCase.isGroupedSlave},
    </#if>
    <#if ctsCase.isGroupedMaster??>
        "isGroupedMaster" : ${ctsCase.isGroupedMaster},
    </#if>
    <#if ctsCase.masterNodeRef??>
        "masterNodeRef" : ${ctsCase.masterNodeRef},
    </#if>
    <#if ctsCase.foiMinisterSignOff??>
        "foiMinisterSignOff" : ${ctsCase.foiMinisterSignOff?string("true", "false")},
    </#if>
    <#if ctsCase.foiIsEir??>
        "foiIsEir" : ${ctsCase.foiIsEir?string("true", "false")},
    </#if>
    <#if ctsCase.exemptions??>
        "exemptions" : ${ctsCase.exemptions},
    </#if>
    <#if ctsCase.pitExtension??>
        "pitExtension" : ${ctsCase.pitExtension?string("true", "false")},
    </#if>
    <#if ctsCase.pitLetterSentDate??>
        "pitLetterSentDate" : "${ctsCase.pitLetterSentDate?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.pitQualifiedExemptions??>
        "pitQualifiedExemptions" : ${ctsCase.pitQualifiedExemptions},
    </#if>
    <#if ctsCase.foiDisclosure??>
        "foiDisclosure" : ${ctsCase.foiDisclosure?string("true", "false")},
    </#if>
    <#if ctsCase.acpoConsultation??>
        "acpoConsultation" : ${ctsCase.acpoConsultation?string("true", "false")},
    </#if>
    <#if ctsCase.cabinetOfficeConsultation??>
        "cabinetOfficeConsultation" : ${ctsCase.cabinetOfficeConsultation?string("true", "false")},
    </#if>
    <#if ctsCase.nslgConsultation??>
        "nslgConsultation" : ${ctsCase.nslgConsultation?string("true", "false")},
    </#if>
    <#if ctsCase.royalsConsultation??>
        "royalsConsultation" : ${ctsCase.royalsConsultation?string("true", "false")},
    </#if>
    <#if ctsCase.roundRobinAdviceConsultation??>
        "roundRobinAdviceConsultation" : ${ctsCase.roundRobinAdviceConsultation?string("true", "false")},
    </#if>
    <#if ctsCase.allocateTarget??>
        "allocateTarget" : "${ctsCase.allocateTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.draftResponseTarget??>
        "draftResponseTarget" : "${ctsCase.draftResponseTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.scsApprovalTarget??>
        "scsApprovalTarget" : "${ctsCase.scsApprovalTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.finalApprovalTarget??>
        "finalApprovalTarget" : "${ctsCase.finalApprovalTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.responderHubTarget??>
        "responderHubTarget" : "${ctsCase.responderHubTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.allocateToResponderTarget??>
        "allocateToResponderTarget" : "${ctsCase.allocateToResponderTarget?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.hmpoResponse??>
      "hmpoResponse" : ${ctsCase.hmpoResponse},
    </#if>
     <#if ctsCase.typeOfCorrespondent??>
      "typeOfCorrespondent" : ${ctsCase.typeOfCorrespondent},
    </#if>
    <#if ctsCase.hmpoStage??>
      "hmpoStage" : ${ctsCase.hmpoStage},
    </#if>
    <#if ctsCase.replyToCorrespondent??>
      "replyToCorrespondent" : ${ctsCase.replyToCorrespondent?string("true", "false")},
    </#if>
    <#if ctsCase.typeOfComplainant??>
      "typeOfComplainant" : ${ctsCase.typeOfComplainant},
    </#if>
    <#if ctsCase.typeOfRepresentative??>
      "typeOfRepresentative" : ${ctsCase.typeOfRepresentative},
    </#if>
    <#if ctsCase.typeOfThirdParty??>
      "typeOfThirdParty" : ${ctsCase.typeOfThirdParty},
    </#if>
    <#if ctsCase.consentAttached??>
      "consentAttached" : ${ctsCase.consentAttached?string("true", "false")},
    </#if>
    <#if ctsCase.replyToApplicant??>
      "replyToApplicant" : ${ctsCase.replyToApplicant?string("true", "false")},
    </#if>
    <#if ctsCase.applicantTitle??>
      "applicantTitle" : ${ctsCase.applicantTitle},
    </#if>
    <#if ctsCase.applicantForename??>
      "applicantForename" : ${ctsCase.applicantForename},
    </#if>
    <#if ctsCase.applicantSurname??>
      "applicantSurname" : ${ctsCase.applicantSurname},
    </#if>
    <#if ctsCase.applicantAddressLine1??>
      "applicantAddressLine1" : ${ctsCase.applicantAddressLine1},
    </#if>
    <#if ctsCase.applicantAddressLine2??>
      "applicantAddressLine2" : ${ctsCase.applicantAddressLine2},
    </#if>
    <#if ctsCase.applicantAddressLine3??>
      "applicantAddressLine3" : ${ctsCase.applicantAddressLine3},
    </#if>
    <#if ctsCase.applicantPostcode??>
      "applicantPostcode" : ${ctsCase.applicantPostcode},
    </#if>
    <#if ctsCase.applicantCountry??>
      "applicantCountry" : ${ctsCase.applicantCountry},
    </#if>
    <#if ctsCase.applicantEmail??>
      "applicantEmail" : ${ctsCase.applicantEmail},
    </#if>
    <#if ctsCase.applicantTelephone??>
      "applicantTelephone" : ${ctsCase.applicantTelephone},
    </#if>
    <#if ctsCase.replyToComplainant??>
      "replyToComplainant" : ${ctsCase.replyToComplainant?string("true", "false")},
    </#if>
    <#if ctsCase.complainantTitle??>
      "complainantTitle" : ${ctsCase.complainantTitle},
    </#if>
    <#if ctsCase.complainantForename??>
      "complainantForename" : ${ctsCase.complainantForename},
    </#if>
    <#if ctsCase.complainantSurname??>
      "complainantSurname" : ${ctsCase.complainantSurname},
    </#if>
    <#if ctsCase.complainantAddressLine1??>
      "complainantAddressLine1" : ${ctsCase.complainantAddressLine1},
    </#if>
    <#if ctsCase.complainantAddressLine2??>
      "complainantAddressLine2" : ${ctsCase.complainantAddressLine2},
    </#if>
    <#if ctsCase.complainantAddressLine3??>
      "complainantAddressLine3" : ${ctsCase.complainantAddressLine3},
    </#if>
    <#if ctsCase.complainantPostcode??>
      "complainantPostcode" : ${ctsCase.complainantPostcode},
    </#if>
    <#if ctsCase.complainantCountry??>
      "complainantCountry" : ${ctsCase.complainantCountry},
    </#if>
    <#if ctsCase.complainantEmail??>
      "complainantEmail" : ${ctsCase.complainantEmail},
    </#if>
    <#if ctsCase.complainantTelephone??>
      "complainantTelephone" : ${ctsCase.complainantTelephone},
    </#if>
    <#if ctsCase.hmpoRefundDecision??>
      "hmpoRefundDecision" : ${ctsCase.hmpoRefundDecision},
    </#if>
    <#if ctsCase.hmpoRefundAmount??>
      "hmpoRefundAmount" : ${ctsCase.hmpoRefundAmount},
    </#if>
    <#if ctsCase.hmpoComplaintOutcome??>
      "hmpoComplaintOutcome" : ${ctsCase.hmpoComplaintOutcome},
    </#if>
    <#if ctsCase.hmpoPassportNumber??>
      "passportNumber" : ${ctsCase.hmpoPassportNumber},
    </#if>
    <#if ctsCase.hmpoApplicationNumber??>
      "applicationNumber" : ${ctsCase.hmpoApplicationNumber},
    </#if>
    <#if ctsCase.hoCaseOfficer??>
      "hoCaseOfficer" : ${ctsCase.hoCaseOfficer},
    </#if>
    <#if ctsCase.responseDate??>
      "responseDate" : "${ctsCase.responseDate?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.complex??>
      "complex" : ${ctsCase.complex?string("true", "false")},
    </#if>
    <#if ctsCase.newInformationReleased??>
      "newInformationReleased" : ${ctsCase.newInformationReleased?string("true", "false")},
    </#if>
    <#if ctsCase.icoReference??>
      "icoReference" : ${ctsCase.icoReference},
    </#if>
    <#if ctsCase.icoOutcome??>
      "icoOutcome" : ${ctsCase.icoOutcome},
    </#if>
    <#if ctsCase.icoComplaintOfficer??>
      "icoComplaintOfficer" : ${ctsCase.icoComplaintOfficer},
    </#if>
    <#if ctsCase.icoOutcomeDate??>
      "icoOutcomeDate" : "${ctsCase.icoOutcomeDate?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.tsolRep??>
      "tsolRep" : ${ctsCase.tsolRep},
    </#if>
    <#if ctsCase.appellant??>
      "appellant" : ${ctsCase.appellant},
    </#if>
    <#if ctsCase.hoJoined??>
      "hoJoined" : ${ctsCase.hoJoined?string("true", "false")},
    </#if>
    <#if ctsCase.tribunalOutcome??>
      "tribunalOutcome" : ${ctsCase.tribunalOutcome},
    </#if>
    <#if ctsCase.tribunalOutcomeDate??>
      "tribunalOutcomeDate" : "${ctsCase.tribunalOutcomeDate?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.enforcementNoticeNeeded??>
      "enforcementNoticeNeeded" : ${ctsCase.enforcementNoticeNeeded?string("true", "false")},
    </#if>
    <#if ctsCase.enforcementNoticeDeadline??>
      "enforcementNoticeDeadline" : "${ctsCase.enforcementNoticeDeadline?datetime?iso("UTC")}",
    </#if>
    <#if ctsCase.organisation??>
      "organisation" : ${ctsCase.organisation},
    </#if>
    <#if ctsCase.answeringMinister??>
      "answeringMinister" : ${ctsCase.answeringMinister},
    </#if>
    <#if ctsCase.answeringMinisterId??>
      "answeringMinisterId" : ${ctsCase.answeringMinisterId},
    </#if>

    "groupedCases" : [
        <#list ctsCase.groupedCases as groupedCase>
            {
                "folderName": "${groupedCase.name}",
                "id" : "${groupedCase.id}",
                "correspondenceType" : "${groupedCase.correspondenceType}",
                <#if groupedCase.uin??>
                    "uin" : "${groupedCase.uin}",
                </#if>
                <#if groupedCase.questionText??>
                    "questionText" : "${groupedCase.questionText}",
                </#if>
                <#if groupedCase.questionText??>
                    "member" : "${groupedCase.member}",
                </#if>
                "urnSuffix" : "${groupedCase.urnSuffix}"
            }
            <#if !(groupedCase == ctsCase.groupedCases?last)>,</#if>
        </#list>
    ],
    <#if ctsCase.isLinkedCase??>
        "isLinkedCase" : ${ctsCase.isLinkedCase},
    </#if>
    "linkedCases" : [
        <#list ctsCase.linkedCases as linkedCase>
            {
                "folderName": "${linkedCase.name}",
                "id" : "${linkedCase.id}",
                "correspondenceType" : "${linkedCase.correspondenceType}",
                "urnSuffix" : "${linkedCase.urnSuffix}"
            }
            <#if !(linkedCase == ctsCase.linkedCases?last)>,</#if>
        </#list>
    ],

    "urnSuffix" : ${ctsCase.urnSuffix},


    <@ctsLib.allowableactions node=node/>


}
