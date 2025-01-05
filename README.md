# ChattyNukkit Plugin

**ChattyNukkit** — это мощный и настраиваемый чат-плагин для Nukkit-серверов. Плагин предоставляет возможности модерации, формата чата, уведомлений, личных сообщений и многое другое.

## Основной функционал

- **Поддержка нескольких чатов**: Локальный, глобальный и пользовательские чаты.
- **Модерация чата**: Автоматический фильтр капслока, запрещенных слов, рекламных ссылок и IP-адресов.
- **Уведомления**: Настраиваемые уведомления в чате, через ActionBar или Title.
- **Команды**:
  - Личные сообщения между игроками.
  - Очищение чата.
  - Шпионский режим для мониторинга локального чата.
  - Игнорирование определенных чатов.
- **Интеграция с LuckPerms** для отображения префиксов и суффиксов.
- **Локализация**: Настраиваемые языковые файлы (`rus`, `eng` и пользовательские).


## Прошу обратить внимание: 
**Плагин находится в разработке. Пожалуйста, сообщайте о найденных багах и недоработках)**

---

## Установка

1. Скачайте `ChattyNukkit.jar` и поместите его в папку `plugins/` вашего Nukkit-сервера.
2. Запустите сервер, чтобы плагин сгенерировал файлы конфигурации.
3. Настройте конфигурационные файлы в папке `plugins/ChattyNukkit/` по своему усмотрению.
4. Перезапустите сервер или перезагрузите плагин.

---

## Конфигурация

### Основные настройки

Файл: `config.yml`

- **Язык**: Установите язык плагина в строке `language`. По умолчанию: `eng`.
- **Чаты**: Вы можете добавить собственные чаты, изменить символы для использования, префиксы, радиус и разрешения.
- **Модерация**: Настройки защиты от капслока, запрещенных слов и рекламы.
- **Уведомления**: Настройка уведомлений через чат, ActionBar или Title.

### Пример настройки чатов
<pre><code>chats:
  custom:
    enable: true
    display-name: &#39;Custom Chat&#39;
    prefix: &#39;§l§6[§eCUSTOM§6]&#39;
    range: 100
    cooldown: 30
    command: &#39;customchat&#39;
    aliases: [&#39;custchat&#39;]
    permission: &#39;chat.custom&#39;
</code></pre>
</div>

<hr>

<h2 id="команды">Команды</h2>

<h3 id="chatty-reload"><code>/chatty reload</code></h3>

<ul>
<li><strong>Разрешение:</strong> <code>chatty.reload</code></li>
<li><strong>Описание:</strong> Перезагрузка плагина.</li>
</ul>

<h3 id="clearchat"><code>/clearchat</code></h3>

<ul>
<li><strong>Разрешение:</strong> <code>chatty.command.clearchat</code></li>
<li><strong>Описание:</strong> Очистка чата.</li>
</ul>

<h3 id="spy"><code>/spy</code></h3>

<ul>
<li><strong>Разрешение:</strong> <code>chatty.command.spy</code></li>
<li><strong>Описание:</strong> Включение/выключение шпионского режима.</li>
</ul>

<h3 id="msg-игрок-сообщение"><code>/msg &lt;игрок&gt; &lt;сообщение&gt;</code></h3>

<ul>
<li><strong>Разрешение:</strong> <code>chatty.command.msg</code></li>
<li><strong>Описание:</strong> Отправить личное сообщение.</li>
</ul>

<h3 id="chatignore-chat"><code>/chatignore &lt;chat&gt;</code></h3>

<ul>
<li><strong>Разрешение:</strong> <code>chatty.command.chatignore</code></li>
<li><strong>Описание:</strong> Игнорирование определенного чата.</li>
</ul>

<hr>

<h2 id="разрешения">Разрешения</h2>

<ul>
<li><code>chat.*</code><strong>: Дает доступ к написанию во все чаты.</strong></li>
<li><code>chat.{chat}</code><strong>: Дает доступ к написанию в конкретный чат.</strong></li>
<li><code>chat.see.*</code><strong>: Дает доступ к просмотру всех чатов.</strong></li>
<li><code>chat.see.{chat}</code><strong>: Дает доступ к просмотру конкретного чата.</strong></li>
<li><code>chatty.command.msg</code><strong>: Отправка личных сообщений.</strong></li>
<li><code>chatty.command.spy</code><strong>: Использование шпионского режима.</strong></li>
<li><code>chatty.command.chatignore</code><strong>: Игнорирование определенных чатов.</strong></li>
<li><code>chatty.command.clearchat</code><strong>: Очистка чата с помощью команды.</strong></li>
<li><code>chatty.moderation.*</code><strong>: Обход всех фильтров модерации.</strong></li>
<li><code>chatty.moderation.bypass.caps</code><strong>: Обход защиты от капслока.</strong></li>
<li><code>chatty.moderation.bypass.badwords</code><strong>: Обход фильтрации запрещенных слов.</strong></li>
<li><code>chatty.moderation.bypass.advertisement</code><strong>: Обход фильтрации рекламы.</strong></li>
<li><code>chatty.notification.chat.default</code><strong>: Разрешение на получение уведомлений в чате.</strong></li>
<li><code>chatty.notification.actionbar</code><strong>: Разрешение на получение уведомлений в ЭкшнБаре</strong></li>
<li><code>chatty.notification.title.default</code><strong>: Разрешения на получение Тайтлов.</strong></li>
<li><code>chatty.notification.*</code><strong>: Все разрешения на получение уведомлений.</strong></li>
<li><code>chatty.misc.joinmessage</code><strong>: Разрешает видеть сообщение о входе.</strong></li>
<li><code>chatty.misc.quitmessage</code><strong>: Разрешает видеть сообщение о выходе.</strong></li>
<li><code>chatty.misc.deathmessage</code><strong>: Разрешает видеть сообщение о смерти.</strong></li>
</ul>

<hr>

<h2 id="зависимости">Зависимости</h2>

<ul>
<li><strong>LuckPerms API</strong>: Для отображения префиксов, суффиксов и работы с группами.</li>
<li><strong>PaPI</strong>: Для управления плагинами и их взаимодействиями.</li>
</ul>

<hr>

<h2 id="замечания">Замечания</h2>

<ol>
<li>В файле <code>plugin.yml</code> убедитесь, что все команды и разрешения настроены правильно.</li>
<li>Если вы добавляете собственные чаты или команды, не забудьте указать соответствующие разрешения в конфигурации.</li>
</ol>

<hr>

<h2 id="сборка-плагина-самостоятельно">Сборка плагина самостоятельно</h2>

<p>Чтобы собрать плагин самостоятельно, следуйте этим шагам:</p>

<ol>
<li>Убедитесь, что у вас установлены JDK и Maven.</li>
<li><p>Склонируйте репозиторий:</p>

<div class="codehilite">
<pre><span></span><code>git<span class="w"> </span>clone<span class="w"> </span>https://github.com/kaleert/ChattyNukkit.git
<span class="nb">cd</span><span class="w"> </span>ChattyNukkit
</code></pre>
</div></li>
<li><p>Постройте проект:</p>

<div class="codehilite">
<pre><span></span><code>mvn<span class="w"> </span>clean<span class="w"> </span>package
</code></pre>
</div></li>
<li><p>После успешной сборки найдите скомпилированный JAR-файл в каталоге <code>target</code>.</p></li>
</ol>

<hr>

<p><strong>Спасибо за использование ChattyNukkit!</strong>
