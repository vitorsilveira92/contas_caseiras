package com.contascaseiras.app.ui.theme

import androidx.annotation.StringRes
import com.contascaseiras.app.R
import com.contascaseiras.app.domain.model.Categoria

@StringRes
fun Categoria.labelRes(): Int = when (this) {
    Categoria.MORADIA -> R.string.categoria_moradia
    Categoria.ALIMENTACAO -> R.string.categoria_alimentacao
    Categoria.TRANSPORTE -> R.string.categoria_transporte
    Categoria.LAZER -> R.string.categoria_lazer
    Categoria.SAUDE -> R.string.categoria_saude
    Categoria.OUTRAS -> R.string.categoria_outras
}
