# RSShool2021-Android-task-Pomodoro
Rolling Scopes School - Android 2021 - Stage 1 - Task 3 - Pomodoro

:wave: Привет всем! 

Практически каждый из нас сталкивался с "нехваткой времени" :alarm_clock: - магическим образом происходит так, что мы начинаем по-настоящему работать тогда, когда уже невозможно игнорировать дедлайны. 

It's fine :dog::fire:. Но хорошая новость в том, что есть множество техник, которые помогают реорганизовать время и способ ведения дел. Как итог мы успеваем гораздо больше. [Pomodoro technique](https://en.wikipedia.org/wiki/Pomodoro_Technique) из этой серии.

В этом задании мы создадим простое приложение - Pomodoro Timer :tomato:. Из тех-стека используем RecyclerView, Custom View, Service, Notification...

Результат должен получиться примерно такой:

[См. видео на youtube](https://youtu.be/4fpBxq3mxa0)


## Описание задания

Один экран. На экране список RecyclerView и форма для создания таймера. Элемент списка - созданный таймер:

<img alt="Pomodoro screen" src="/img/pomodoro_screen.png" width="400"/>

Состав элемента списка слева-направо:
1. мигающий индикатор (чуть анимации в наш проект) - видим только тогда, когда таймер включен.
2. таймер обратного отсчёта типа hh:mm:ss
3. Custom view. Представляет из себя своеобразный Progress bar - постепенно рисуется круг. View визуально отображает ход таймера. Скажем, для таймера на 10 минут, если таймер еще не запускался - View пустая. Если таймер отсчитал 5 минут - View наполовину нарисована. Когда прошло 10 минут - View нарисована полностью. Шаг - секунда или меньше. В своей реализации необязательно использовать круг, можно сделать линейный Progress bar или ещё какой-то. Главное требование - это должно быть Custom View и каким-либо образом визуализировать время таймера.
4. кнопка `Start`/`Stop` - запуск или остановка таймера. `Start` - запускает данный таймер и при этом останавливает любой другой запущенный таймер. Т.е. одновременно может работать только один таймер в списке. "Запуск таймера" означает начало обратного отсчёта таймера, начало мигания индикатора и начало рисования Custom View. После старта таймера кнопка меняет текст на `Stop`. Нажатие на `Stop` отстанавливает таймер (время сохраняется), мигающий индикатор и рисование Custom View
5. кнопка `Delete` - удаляет данный таймер из списка.

Это обязательный состав элемента списка для данного задания. Но вы вольны добавить что-то ещё, например, добавить кнопку сброса времени таймера, использовать красивый дизайн и т.д., но всё в разумных рамках :point_up:

Форма для создания таймера находится под списком и включает:
1. поле, или какой-либо picker, для задания количества минут (+ на ваше усмотрение, то же для секунд).
2. кнопка `Add Timer` - добавляет таймер в список. После добавления таймер не активируется.

Таймер, который завершил свою работу, должен как-то сигнализировать об этом пользователю. Давайте для простоты будем использовать хотя бы один из трех вариатов:
1. звуковой сигнал
2. изменение цвета элемента списка
3. Toast

Если в данный момент есть запущенный таймер и приложение сворачивается или стартует другое приложение - т.е. Pomodoro Timer App уходит в background, то запускается [Foreground Service](https://developer.android.com/guide/components/foreground-services), который выводит Notification c текущим значением таймера. Значение времени на нотификации продолжает отсчитываться.

<img alt="Pomodoro screen" src="/img/notification.png" width="400"/>

Если вам показалось, что задание слишком сложное для вашего уровня - не переживайте! Будет три примера исходниками - создание RecyclerView с таймером, создание Custom View и создание Foreground Service c Notification. Останется объединить все это в одном проекте:

1. Пример Recycler View - [смотри тут](https://ziginsider.github.io/Simple-RecyclerView-StopwatchApp/)
2. Пример Custom View - [смотри тут](https://ziginsider.github.io/Simple-Custom-View/)
3. Пример Foreground Service - [смотри тут](https://ziginsider.github.io/Foreground-Service/)

 
## Cross-checking

- Изучите требования к <a href="https://docs.rs.school/#/cross-check-flow?id=cross-check">cross-check</a>
- Форму для оценки задания по критериям ищите <a href="https://ziginsider.github.io/checklist/index.html">здесь</a> ⚡️

Успехов! 🤞




