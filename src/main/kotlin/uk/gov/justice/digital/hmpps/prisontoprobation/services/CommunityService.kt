package uk.gov.justice.digital.hmpps.prisontoprobation.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

@Service
open class CommunityService(@Qualifier("communityApiRestTemplate") private val restTemplate: RestTemplate) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  open fun updateProbationCustody(offenderNo: String, bookingNo: String, updateCustody: UpdateCustody): Custody? {
    return try {
      val response = restTemplate.exchange("/secure/offenders/nomsNumber/{nomsNumber}/custody/bookingNumber/{bookingNumber}", HttpMethod.PUT, HttpEntity(updateCustody), Custody::class.java, offenderNo, bookingNo)
      response.body!!
    } catch (e: HttpClientErrorException) {
      if (e.statusCode != HttpStatus.NOT_FOUND) throw e
      log.info("Booking {} not found for {} message is {}", bookingNo, offenderNo, e.responseBodyAsString)
      null
    }
  }

  open fun updateProbationCustodyBookingNumber(offenderNo: String, updateCustodyBookingNumber: UpdateCustodyBookingNumber): Custody? {
    return try {
      val response = restTemplate.exchange("/secure/offenders/nomsNumber/{nomsNumber}/custody/bookingNumber", HttpMethod.PUT, HttpEntity(updateCustodyBookingNumber), Custody::class.java, offenderNo)
      response.body!!
    } catch (e: HttpClientErrorException) {
      if (e.statusCode != HttpStatus.NOT_FOUND) throw e
      log.info("Booking not found for {} message is {}", offenderNo, e.responseBodyAsString)
      null
    }
  }
  open fun replaceProbationCustodyKeyDates(offenderNo: String, bookingNo: String, replaceCustodyKeyDates: ReplaceCustodyKeyDates): Custody? {
    return try {
      val response = restTemplate.exchange("/secure/offenders/nomsNumber/{nomsNumber}/bookingNumber/{bookingNo}/custody/keyDates", HttpMethod.POST, HttpEntity(replaceCustodyKeyDates), Custody::class.java, offenderNo, bookingNo)
      response.body!!
    } catch (e: HttpClientErrorException) {
      if (e.statusCode != HttpStatus.NOT_FOUND) throw e
      log.info("Booking not found for {} offender {} message is {}", bookingNo, offenderNo, e.responseBodyAsString)
      null
    }
  }
}

data class UpdateCustody(
    val nomsPrisonInstitutionCode: String
)

data class Institution(
    val description: String?
)

data class Custody(
    val institution: Institution,
    val bookingNumber: String
)

data class UpdateCustodyBookingNumber(
    val sentenceStartDate: LocalDate,
    val bookingNumber: String
)

data class ReplaceCustodyKeyDates(
    val conditionalReleaseDate: LocalDate? = null,
    val licenceExpiryDate: LocalDate? = null,
    val hdcEligibilityDate: LocalDate? = null,
    val paroleEligibilityDate: LocalDate? = null,
    val sentenceExpiryDate: LocalDate? = null,
    val expectedReleaseDate: LocalDate? = null,
    val postSentenceSupervisionEndDate: LocalDate? = null
)