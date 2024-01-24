/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.tractusx.bpdmcertificatemanagement.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.eclipse.tractusx.bpdmcertificatemanagement.dto.request.CertificateDocumentRequestDto
import org.eclipse.tractusx.bpdmcertificatemanagement.dto.request.PaginationRequest
import org.eclipse.tractusx.bpdmcertificatemanagement.dto.response.BpnCertifiedCertificateResponse
import org.eclipse.tractusx.bpdmcertificatemanagement.dto.response.CertificateDocumentResponseDto
import org.eclipse.tractusx.bpdmcertificatemanagement.dto.response.CertificateResponseDto
import org.eclipse.tractusx.bpdmcertificatemanagement.dto.response.PageDto
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import java.util.*

@RequestMapping("/api/catena", produces = [MediaType.APPLICATION_JSON_VALUE])
@HttpExchange("/api/catena")
interface CertificateApi {

    @Operation(
        summary = "Provide a specific certificate document for a given BPN.",
        operationId = "setCertificateDocument",
        description = "Use this API call to provide a specific certificate document for a given BPN.",
        responses = [
            ApiResponse(responseCode = "201", description = "Certificate created successfully"),
            ApiResponse(responseCode = "400", description = "Malformed URL", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
            ApiResponse(responseCode = "422", description = "Unprocessable Entity ", content = [Content()]),
            ApiResponse(responseCode = "406", description = "Document not available", content = [Content()]),
            ApiResponse(responseCode = "503", description = "Service not available", content = [Content()])
        ]
    )
    @PostMapping("/certificate/document")
    @PostExchange("/certificate/document")
    fun setCertificateDocument(@RequestBody certificateDocumentRequestDto: CertificateDocumentRequestDto): ResponseEntity<CertificateDocumentResponseDto>


    @Operation(
        summary = "Request a specific certificate document for a given Document ID.",
        operationId = "retrieveCertificateDocument",
        description = "This endpoint call to request a specific certificate document for a given Document ID.",
        responses = [
            ApiResponse(responseCode = "200", description = "Document for the given id"),
            ApiResponse(responseCode = "400", description = "Malformed URL", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Certificate Document ID not found", content = [Content()]),
            ApiResponse(responseCode = "406", description = "Document not available", content = [Content()]),
            ApiResponse(responseCode = "503", description = "Service not available", content = [Content()])
        ]
    )
    @GetMapping("/certificate/document/{cdID}")
    @GetExchange("/certificate/document/{cdID}")
    fun getCertificateDocument(@Parameter(description = "Certificate document ID") @PathVariable cdID: UUID): ResponseEntity<CertificateDocumentResponseDto>


    @Operation(
        summary = "Get all certificates of a given BPN.",
        operationId = "getCertificatesByBpnPaginated",
        description = "This endpoint retrieves all certificates available for the inserted BPN. " +
                "In case of BPNL,  all certificates available for the BPN are returned. " +
                "In case of a BPNS, all certificates which either are assigned to the BPNS or the matching BPNL enclosing BPNS are returned.",
        responses = [
            ApiResponse(responseCode = "200", description = "Page of certificates matching the search criteria, may be empty"),
            ApiResponse(responseCode = "400", description = "On malformed pagination request", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Business Partner number not found", content = [Content()]),
            ApiResponse(responseCode = "503", description = "Service not available", content = [Content()])
        ]
    )
    @GetMapping("/certificate/{bpn}")
    @GetExchange("/certificate/{bpn}")
    fun getCertificatesByBpnPaginated(
        @Parameter(
            description = "BPN value, It can be BPNL, BPNS, BPNA",
            required = true
        ) @PathVariable("bpn") bpn: String, @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<CertificateResponseDto>

    @Operation(
        summary = "Get a specific certificate by certificate type and a given Business Partner Number.",
        operationId = "getCertificateByTypeAndBpnPaginated",
        description = "This endpoint retrieves certificate based on certificate type for provided business partner number.",
        responses = [
            ApiResponse(responseCode = "200", description = "Certificate for business partner and specified certificate type, " +
                    "can be the case where provided bpn or certificate type could not be found"),
            ApiResponse(responseCode = "400", description = "On malformed request", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Certificate type or business partner number not found", content = [Content()]),
            ApiResponse(responseCode = "503", description = "Service not available", content = [Content()])
        ]
    )
    @GetMapping("/certificate/{bpn}/{certificateType}")
    @GetExchange("/certificate/{bpn}/{certificateType}")
    fun getCertificateByTypeAndBpnPaginated(
        @Parameter(description = "BPN value, It can be BPNL, BPNS, BPNA", required = true) @PathVariable("bpn") bpn: String,
        @Parameter(description = "Certificate type e.g. IATF-16949", required = true) @PathVariable("certificateType") certificateType: String,
        @ParameterObject paginationRequest: PaginationRequest
    ): PageDto<CertificateResponseDto>

    @Operation(
        summary = " Gets info whether BPN is certified for a specific certificate type.",
        operationId = "checkCertificateByBpnAndType",
        description = "This endpoint checks whether a provided BPN is certified for a specific certificate type.",
        responses = [
            ApiResponse(responseCode = "201", description = "Certificate list for BP"),
            ApiResponse(responseCode = "400", description = "Malformed URL", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Certificate type or business partner number not found", content = [Content()]),
            ApiResponse(responseCode = "503", description = "Service not available", content = [Content()])
        ]
    )
    @GetMapping("/certificate/simple/{bpn}/{certificateType}")
    @GetExchange("/certificate/simple/{bpn}/{certificateType}")
    fun checkCertificateByBpnAndType(
        @Parameter(description = "Business Partner Number", required = true) @PathVariable bpn: String,
        @Parameter(description = "Certificate Type", required = true) @PathVariable certificateType: String
    ): List<BpnCertifiedCertificateResponse>

}