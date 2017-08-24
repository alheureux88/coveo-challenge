package com.coveo.filters

import javax.inject.Inject

import com.coveo.utils.{LoggingFilter, RequestIDFilter}
import play.api.http.{DefaultHttpFilters, EnabledFilters}
import play.filters.gzip.GzipFilter

class AllFilters @Inject()(
                         defaultFilters: EnabledFilters,
                         gzip: GzipFilter,
                         requestIDFilter: RequestIDFilter,
                         log: LoggingFilter
                       ) extends DefaultHttpFilters(defaultFilters.filters :+ gzip :+ requestIDFilter :+ log: _*)