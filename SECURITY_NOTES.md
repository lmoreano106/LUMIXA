# Seguridad de Firebase/Gemini (LUMIXA)

## Gemini API Key
- `GEMINI_API_KEY` se obtiene desde `local.properties` en tiempo de build (`BuildConfig.GEMINI_API_KEY`).
- No hardcodear claves en código Kotlin/XML.
- `local.properties` debe permanecer fuera del repositorio (ya cubierto por `.gitignore`).
- Rotar la clave si se sospecha exposición.

## Evolución recomendada (sin romper el flujo actual)
Para una versión futura, mover llamadas a Gemini a backend/Cloud Functions para:
- Evitar exponer la clave al cliente Android.
- Aplicar rate limiting y auditoría centralizada.
- Introducir validación server-side por usuario/rol.

> Esta app mantiene el flujo actual por compatibilidad con la versión estable.
