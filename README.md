# Contas Caseiras

App de gestão financeira doméstica para Android. Dados salvos localmente no
aparelho, sem login obrigatório; versão Pro (assinatura mensal) libera
relatórios e remove anúncios.

## Stack

- Kotlin + Jetpack Compose (Material 3)
- Room (SQLite local-first) — `app/src/main/java/.../data/local`
- Hilt para injeção de dependência
- AlarmManager para lembretes locais antes do vencimento
- Navigation Compose

## Arquitetura

MVVM + Repository:

```
ui/            -> Composables + ViewModels (Home, Add/Edit)
data/local/    -> Room (entity, dao, converters, database)
data/repository/ -> abstrai o DAO para os ViewModels
domain/model/  -> enums de domínio (Categoria, TipoTransacao, StatusPagamento)
notification/  -> ReminderScheduler (AlarmManager), ReminderReceiver, BootReceiver
di/            -> módulos Hilt
```

## Status do MVP

| Requisito do MVP | Status |
|---|---|
| Adicionar entrada/saída | ✅ implementado |
| Marcar pago/pendente | ✅ implementado |
| Categorias fixas | ✅ implementado (enum; categorias custom ficam para o Pro) |
| Lista mensal + navegação entre meses | ✅ implementado |
| Calendário visual | ⬜ pendente — hoje é lista; grade de calendário é Fase 1.1 |
| Saldo previsto do mês | ✅ implementado |
| Lembrete local antes do vencimento | ✅ implementado (AlarmManager + notificação) |
| Dados no celular, sem login obrigatório | ✅ implementado (Room local, sem auth) |

## Login Google / monetização (ainda não implementados)

Ficaram fora deste primeiro commit porque dependem de configuração externa
(Console do Google Cloud, AdMob, Play Console) que só o responsável pelo
projeto pode gerar:

- **Login com Google**: só deve ser pedido quando o usuário for assinar o
  Pro. Falta configurar OAuth client ID no Google Cloud Console e integrar
  Credential Manager API.
- **AdMob**: falta criar o app no AdMob e colocar o App ID real no
  `AndroidManifest.xml` (há um `TODO` marcando onde) antes de adicionar os
  banners nas telas do free.
- **Google Play Billing**: assinatura mensal Pro — falta cadastrar o produto
  na Play Console antes de integrar a biblioteca de billing.

## Build

Este ambiente de desenvolvimento não tem acesso a `dl.google.com` (bloqueado
pela política de rede do container), então o projeto não foi compilado aqui.
Para rodar:

1. Abrir a pasta no Android Studio (Ladybug ou mais recente).
2. Deixar o Gradle sincronizar (`./gradlew` já está configurado, Gradle 8.9 +
   AGP 8.7.2).
3. Rodar em um emulador/aparelho com Android 8.0+ (minSdk 26).

## Roadmap

1. ~~Fase 0 — Setup do projeto~~
2. **Fase 1 — MVP core** (este commit): CRUD, categorias, status, lista
   mensal, saldo previsto, lembretes
   - Fase 1.1: grade de calendário visual (hoje é lista)
3. Fase 2 — Polimento de UX da Fase 1 (validações, estados vazios, testes)
4. Fase 3 — Monetização: Google Sign-In (vinculado à assinatura), AdMob no
   free, Google Play Billing para o Pro
5. Fase 4 — Recursos Pro: relatórios com gráficos, categorias personalizadas
6. Fase 5 — Publicação: ícone final, screenshots, política de privacidade,
   Data Safety form, build assinado, upload na Play Console
