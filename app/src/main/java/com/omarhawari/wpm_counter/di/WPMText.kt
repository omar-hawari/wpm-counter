package com.omarhawari.wpm_counter.di

import javax.inject.Inject

/**
*  WPMText that encapsulates a string value in order to make it injectable and thus testable
 *  @param value: The string value of the WPMText
* */
class WPMText @Inject constructor(val value: String)