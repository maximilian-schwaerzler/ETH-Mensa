package com.github.maximilianschwaerzler.ethuzhmensa.data2

import android.content.Context
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MenuDao
import com.github.maximilianschwaerzler.ethuzhmensa.network.services.CookpitMenuService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MenuRepository2 @Inject constructor(
    private val menuService: CookpitMenuService,
    private val menuDao: MenuDao,
    @ApplicationContext private val appContext: Context
) {

}