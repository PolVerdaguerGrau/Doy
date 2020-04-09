package com.napptilians.commons

interface Mapper<in FROM, out TO> {

    fun map(from: FROM?): TO
}
