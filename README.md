# CentroTareas

**Estudiante:** Angieluz Vasquez Macalupu

## Descripción

CentroTareas es una aplicación Android que permite ejecutar una tarea con progreso y cancelación, contar números primos y comparar cargas de datos simuladas de forma secuencial y paralela. Su objetivo es demostrar cómo realizar tareas prolongadas sin bloquear el hilo principal y mantener la pantalla disponible para responder a los toques. Está desarrollada en Kotlin con Jetpack Compose y Material 3, siguiendo la arquitectura MVVM con Repository. Utiliza StateFlow para comunicar los estados a la pantalla, viewModelScope para gestionar las corrutinas, Dispatchers.Default para el cálculo de primos y Job para cancelar tareas. Además, emplea async y await dentro de coroutineScope para ejecutar cargas simultáneas. Los datos son ficticios y las cargas se simulan con delay, incluyendo una opción para provocar errores y reintentar.

## Árbol de paquetes

```text
com.tecsup.centrotareas/
├── MainActivity.kt
├── model/
│   └── Estados.kt
├── data/
│   ├── TareasRepository.kt
│   └── TareasRepositoryLocal.kt
├── viewmodel/
│   ├── TareasViewModel.kt
│   └── TareasViewModelFactory.kt
└── ui/
    ├── PantallaTareas.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Arquitectura MVVM

```text
PantallaTareas
      |
      | Acciones del usuario
      v
TareasViewModel
      |
      | Llamadas al Repository
      v
TareasRepository (interfaz)
      ^
      | Implementa
      |
TareasRepositoryLocal

TareasViewModel ── StateFlow ──> PantallaTareas
```

## Observación del Paso 1

Al tocar el botón Tarea larga (MAL), la pantalla queda congelada durante cuatro segundos y al final solo muestra el mensaje «Terminó». El texto «Trabajando...» no llega a verse porque se bloquea el hilo principal antes de que pueda la pantalla pueda actualizarse. Durante ese tiempo, el botón «Tócame» tampoco responde.

Si la espera fuera de 10 segundos, Android podría mostrar un aviso de que la aplicación no responde.

## Capturas de funcionamiento

<table>
  <tr>
    <td align="center" width="33%" valign="top">
      <strong>Tarea con progreso</strong><br><br>
      <img width="682" height="727" alt="Captura de pantalla 2026-11-17 190416" src="https://github.com/user-attachments/assets/768f9fac-c98a-49e7-b59b-0fdc7bf1011d" />
    </td>
    <td align="center" width="33%" valign="top">
      <strong>Tarea cancelada</strong><br><br>
      <img width="360" height="710" alt="Captura de pantalla 2026-11-17 205348" src="https://github.com/user-attachments/assets/bae6397d-8aa4-43c8-845d-d852b4f6bbb2" />
    </td>
    <td align="center" width="33%" valign="top">
      <strong>Cálculo de primos</strong><br><br>
      <img width="347" height="727" alt="Captura de pantalla 2026-11-17 214309" src="https://github.com/user-attachments/assets/23940aec-cf58-4b73-85bd-fa56eacecf7e" />
    </td>
  </tr>
  <tr>
    <td align="center" width="33%" valign="top">
      <strong>Cargas una por una</strong><br><br>
      <img width="387" height="766" alt="Captura de pantalla 2026-11-17 221433" src="https://github.com/user-attachments/assets/1a629498-296b-4eb7-95b4-d42af4b43389" />
    </td>
    <td align="center" width="33%" valign="top">
      <strong>Cargas a la vez</strong><br><br>
      <img width="380" height="767" alt="Captura de pantalla 2026-11-17 221451" src="https://github.com/user-attachments/assets/0ec9fc5a-0424-4ba1-9376-e4f4df19b689" />
    </td>
    <td align="center" width="33%" valign="top">
      <strong>Error y reintento</strong><br><br>
      <img width="420" height="783" alt="Captura de pantalla 2026-11-17 222749" src="https://github.com/user-attachments/assets/1c94c8dc-17fa-42ab-8b78-87f850acf1ac" />
    </td>
  </tr>
</table>

## Para pensar

### **1. ¿Por qué no se veía «Trabajando...» en el Paso 1?**

Porque cambiar el estado no actualiza la pantalla inmediatamente. Después de asignar «Trabajando...», Thread.sleep bloqueaba el hilo principal durante cuatro segundos, impidiendo que se mostrara el nuevo texto. Cuando terminaba ese tiempo, el estado ya había cambiado a «Terminó», por eso solo se mostraba ese mensaje.

### **2. ¿Qué diferencia hay entre Thread.sleep y delay?**

Thread.sleep bloquea el hilo donde se ejecuta, por lo que si se utiliza en el hilo principal, la pantalla deja de responder y actualizarse. En cambio, delay pausa la corrutina sin bloquear el hilo, permitiendo que se sigan realizando otras tareas. Además, permite cancelar la corrutina.

### **3. ¿Por qué Dispatchers.Default para los primos y Dispatchers.IO para la red?**

Dispatchers.Default se utiliza para tareas que necesitan procesamiento, como contar números primos. En cambio, Dispatchers.IO está orientado a operaciones de entrada y salida, como leer archivos o realizar llamadas de red. En esta aplicación, las cargas se simulan utilizando delay, por lo que no es necesario utilizar Dispatchers.IO.

### **4. ¿Qué pasaría con la corrutina si el usuario cierra la pantalla? ¿Qué papel cumple viewModelScope?**

Las corrutinas iniciadas con viewModelScope se cancelan cuando el ViewModel se elimina definitivamente, evitando que sigan ejecutándose tareas de una pantalla que ya no existe. Sin embargo, si el dispositivo rota, el ViewModel normalmente se mantiene, por lo que la corrutina puede continuar. Si la aplicación pasa a segundo plano, tampoco significa que las corrutinas se cancelen automáticamente.

### **5. ¿Por qué async tarda 3 s y no 5 s? ¿Qué pasa si una de las dos cargas falla?**

Porque ambos async se ejecutan al mismo tiempo, por lo que no es necesario esperar a que termine una carga para iniciar la otra. El perfil tarda dos segundos y los pedidos tres, así que el tiempo total es aproximadamente tres segundos. Si una de las cargas falla, se cancelan las otras tareas que siguen ejecutándose y el error se envía al bloque que lo contiene. El try/catch del ViewModel permite mostrar el estado de error, mientras que CancellationException se vuelve a lanzar para respetar la cancelación.
