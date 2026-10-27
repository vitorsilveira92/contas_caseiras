package com.contascaseiras.app.domain.model

/**
 * Categorias fixas do MVP. Categorias personalizadas são um recurso Pro (Fase 4)
 * e vão exigir uma tabela própria — este enum cobre só o conjunto padrão.
 */
enum class Categoria {
    MORADIA,
    ALIMENTACAO,
    TRANSPORTE,
    LAZER,
    SAUDE,
    OUTRAS,
}
