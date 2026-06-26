<template>
  <div class="social-view page-container">
    <div class="container">
      <div class="social-layout">
        <!-- 左侧：个人信息和发布动态 -->
        <div class="social-sidebar">
          <!-- 个人信息卡片 -->
          <div class="user-profile-card">
            <div class="profile-avatar">
              <img
                v-if="userInfo?.avatar"
                :src="getImageUrl(userInfo.avatar)"
                alt="头像"
                class="avatar-img"
              />
              <div v-else class="avatar-placeholder">
                {{ userInfo?.username?.[0]?.toUpperCase() || '👤' }}
              </div>
            </div>
            <div class="profile-info">
              <div class="profile-name">{{ userInfo?.nickname || userInfo?.username || '用户' }}</div>
              <div v-if="userInfo?.school" class="profile-school">🏫 {{ userInfo.school }}</div>
              <div v-if="userInfo?.bio" class="profile-bio">{{ userInfo.bio }}</div>
              <!-- 总点赞数统计 -->
              <div class="profile-stats">
                <div class="stat-item">
                  <span class="stat-icon">❤️</span>
                  <span class="stat-value">{{ totalLikes }}</span>
                  <span class="stat-label">总获赞</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的动态管理 -->
          <div class="my-posts-section">
            <BaseButton
              type="secondary"
              size="large"
              @click="showMyPosts = !showMyPosts"
            >
              📝 我的动态 ({{ myPostsCount }})
            </BaseButton>
          </div>

          <!-- 发布动态按钮 -->
          <div class="post-creator">
            <BaseButton type="primary" size="large" @click="showPublishModal = true">
              ✏️ 发布动态
            </BaseButton>
          </div>

          <!-- 我的动态列表 -->
          <div v-if="showMyPosts" class="my-posts-list">
            <div v-if="myPosts.length === 0" class="empty-posts">
              <p>还没有发布过动态</p>
            </div>
            <div v-else class="posts-mini-list">
              <div
                v-for="post in myPosts"
                :key="post.id"
                class="mini-post-item"
              >
                <div class="mini-post-content">
                  <p class="mini-post-text">{{ post.content.substring(0, 50) }}{{ post.content.length > 50 ? '...' : '' }}</p>
                  <div class="mini-post-meta">
                    <span class="mini-post-time">{{ formatTime(post.createdAt) }}</span>
                    <span class="mini-post-stats">❤️ {{ post.likes }} 💬 {{ post.comments }}</span>
                  </div>
                </div>
                <div class="mini-post-actions">
                  <button
                    class="action-icon-btn"
                    title="编辑"
                    @click="handleEditPost(post)"
                  >
                    ✏️
                  </button>
                  <button
                    class="action-icon-btn"
                    title="删除"
                    @click="handleDeletePost(post.id)"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中间：动态列表 -->
        <div class="social-main">
          <div class="main-header">
            <h2 class="page-title">运动动态</h2>
            <div class="header-controls">
              <div class="topic-filter">
                <label class="filter-label">
                  <span class="filter-icon">🏷️</span>
                  筛选话题：
                </label>
                <BaseSelect
                  v-model="selectedTopicId"
                  class="topic-select"
                  @update:modelValue="handleTopicChange"
                >
                  <option :value="0">全部话题</option>
                  <option
                    v-for="topic in topics"
                    :key="topic.id"
                    :value="topic.id"
                  >
                    #{{ topic.name }} ({{ topic.postCount }})
                  </option>
                </BaseSelect>
                <button
                  v-if="selectedTopicId > 0"
                  class="clear-filter-btn"
                  @click="clearTopicFilter"
                  title="清除筛选"
                >
                  ✕
                </button>
              </div>
              <div class="sort-controls">
                <button
                  v-for="option in sortOptions"
                  :key="option.value"
                  class="sort-btn"
                  :class="{ active: currentSort === option.value }"
                  @click="changeSort(option.value)"
                >
                  <span class="sort-icon">{{ option.icon }}</span>
                  <span>{{ option.label }}</span>
                </button>
              </div>
            </div>
          </div>
          <div v-if="loading" class="loading">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>
          <div v-else-if="posts.length === 0" class="empty-state">
            <div class="empty-icon">📭</div>
            <p>暂无动态，快来发布第一条吧！</p>
          </div>
          <div v-else class="posts-list">
            <div
              v-for="post in posts"
              :key="post.id"
              :id="`post-${post.id}`"
              class="post-card"
              :class="{
                'post-pinned': post.isPinned,
                'post-featured': post.isFeatured,
                'post-card-highlight': post.id === highlightPostId,
              }"
            >
              <!-- 置顶/精华/热门标记 -->
              <div v-if="post.isPinned || post.isFeatured || post.isHot" class="post-badges">
                <span v-if="post.isPinned" class="badge badge-pinned">📌 置顶</span>
                <span v-if="post.isFeatured" class="badge badge-featured">⭐ 精华</span>
                <span v-if="post.isHot" class="badge badge-hot">🔥 热门</span>
              </div>
              <div class="post-header">
                <div class="post-author">
                  <div class="author-avatar">
                    <img
                      v-if="post.avatar"
                      :src="getImageUrl(post.avatar)"
                      alt="头像"
                      class="avatar-img"
                    />
                    <span v-else>{{ post.username[0] }}</span>
                  </div>
                  <div class="author-info">
                    <div class="author-name">{{ post.username }}</div>
                    <div class="post-time">{{ formatTime(post.createdAt) }}</div>
                  </div>
                </div>
                <div v-if="post.userId === currentUserId" class="post-menu">
                  <button class="menu-btn" @click="handleEditPost(post)">✏️</button>
                  <button class="menu-btn" @click="handleDeletePost(post.id)">🗑️</button>
                </div>
              </div>
              <div class="post-content" @click="handleContentClick($event, post)">
                <p v-html="formatContentWithTopics(post.content)"></p>
                <!-- @好友标签 -->
                <div v-if="getMentionedUsers(post).length > 0" class="post-mentions">
                  <span
                    v-for="(mention, index) in getMentionedUsers(post)"
                    :key="index"
                    class="mention-tag"
                    @click.stop="handleMentionClick(mention)"
                  >
                    @{{ mention }}
                  </span>
                </div>
                <div v-if="post.topics && post.topics.length > 0" class="post-topics">
                  <router-link
                    v-for="topic in post.topics"
                    :key="topic"
                    :to="`/topics/${encodeURIComponent(topic)}`"
                    class="topic-tag"
                  >
                    #{{ topic }}
                  </router-link>
                </div>
                <div v-if="post.venueName" class="post-venue">
                  📍 {{ post.venueName }}
                </div>
                <div v-if="post.images && post.images.length > 0" class="post-images">
                  <img
                    v-for="(img, index) in post.images"
                    :key="index"
                    :src="getImageUrl(img)"
                    alt="动态图片"
                    class="post-image"
                    @click="previewImage(getImageUrl(img))"
                  />
                </div>
                <div v-if="post.video" class="post-video">
                  <video
                    :src="getVideoUrl(post.video)"
                    controls
                    class="post-video-player"
                  ></video>
                </div>
              </div>
              <div class="post-actions">
                <button
                  :class="['action-btn', { 'action-btn--active': post.isLiked }]"
                  @click="handleLike(post.id)"
                >
                  ❤️ {{ post.likes }}
                </button>
                <button class="action-btn" @click="toggleComments(post.id)">
                  💬 {{ post.comments }}
                </button>
              <button class="action-btn" @click="openShare(post)">🔗 分享</button>
              </div>
              <!-- 评论区域 -->
              <div v-if="showComments[post.id]" class="post-comments">
                <div class="comment-form">
                  <input
                    v-model="commentInputs[post.id]"
                    type="text"
                    class="comment-input"
                    :placeholder="replyingTo[post.id] ? `回复 @${replyingTo[post.id] || ''}...` : '写评论...'"
                    @keyup.enter="handleAddComment(post.id)"
                  />
                  <BaseButton
                    type="primary"
                    size="small"
                    @click="handleAddComment(post.id)"
                  >
                    发送
                  </BaseButton>
                  <BaseButton
                    v-if="replyingTo[post.id] && replyingTo[post.id] !== ''"
                    type="default"
                    size="small"
                    @click="cancelReply(post.id)"
                  >
                    取消
                  </BaseButton>
                </div>
                <div class="comments-list">
                  <div
                    v-for="comment in getComments(post.id)"
                    :key="comment.id"
                    class="comment-item"
                  >
                    <div class="comment-header">
                      <div class="comment-author-info">
                        <span class="comment-author">{{ comment.username }}</span>
                        <span v-if="comment.parentCommentUsername" class="comment-reply-to">
                          回复 @{{ comment.parentCommentUsername }}
                        </span>
                      </div>
                      <button
                        class="reply-btn"
                        @click="handleReply(post.id, comment.id, comment.username)"
                      >
                        回复
                      </button>
                    </div>
                    <div class="comment-content">{{ comment.content }}</div>
                    <div class="comment-footer">
                      <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                    </div>
                    <!-- 回复列表 -->
                    <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                      <div
                        v-for="reply in comment.replies"
                        :key="reply.id"
                        class="reply-item"
                      >
                        <div class="comment-header">
                          <div class="comment-author-info">
                            <span class="comment-author">{{ reply.username }}</span>
                            <span v-if="reply.parentCommentUsername" class="comment-reply-to">
                              回复 @{{ reply.parentCommentUsername }}
                            </span>
                          </div>
                          <button
                            class="reply-btn"
                            @click="handleReply(post.id, comment.id, reply.username)"
                          >
                            回复
                          </button>
                        </div>
                        <div class="comment-content">{{ reply.content }}</div>
                        <div class="comment-footer">
                          <span class="comment-time">{{ formatTime(reply.createdAt) }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发布动态弹窗 -->
    <div v-if="showPublishModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content publish-modal">
        <div class="modal-header">
          <div class="modal-header-left">
            <div class="modal-icon">✏️</div>
            <div>
              <h3 class="modal-title">{{ (postForm as any).editingPostId ? '编辑动态' : '发布动态' }}</h3>
              <p class="modal-subtitle">分享你的运动时刻，记录每一次进步</p>
            </div>
          </div>
          <button class="modal-close" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <!-- 用户信息展示 -->
          <div class="user-info-section">
            <div class="user-avatar-mini">
              <img
                v-if="userInfo?.avatar"
                :src="getImageUrl(userInfo.avatar)"
                alt="头像"
                class="avatar-img"
              />
              <div v-else class="avatar-placeholder-mini">
                {{ userInfo?.username?.[0]?.toUpperCase() || '👤' }}
              </div>
            </div>
            <div class="user-info-text">
              <div class="user-name">{{ userInfo?.nickname || userInfo?.username || '我' }}</div>
              <div v-if="userInfo?.school" class="user-school">🏫 {{ userInfo.school }}</div>
            </div>
          </div>

          <!-- 内容输入区 -->
          <div class="form-item content-item">
            <div class="textarea-wrapper">
              <textarea
                ref="contentTextarea"
                v-model="postForm.content"
                class="form-textarea"
                placeholder="今天运动了什么？有什么收获？分享给大家吧... 💪 输入@可以@好友"
                rows="6"
                maxlength="1000"
                @input="handleContentInput"
                @keydown="handleContentKeydown"
              ></textarea>
              <div class="char-counter">
                <span :class="{ 'char-warning': charCount > 900 }">{{ charCount }}</span>
                <span>/1000</span>
              </div>
              <!-- @好友选择下拉列表 -->
              <div v-if="showMentionList" class="mention-list">
                <div
                  v-for="friend in filteredFriends"
                  :key="friend.id"
                  class="mention-item"
                  :class="{ active: selectedMentionIndex === filteredFriends.indexOf(friend) }"
                  @click="selectMention(friend)"
                >
                  <div class="mention-avatar">
                    <img
                      v-if="friend.avatar"
                      :src="getImageUrl(friend.avatar)"
                      alt="头像"
                      class="avatar-img"
                    />
                    <span v-else>{{ (friend.nickname || friend.username)?.[0] }}</span>
                  </div>
                  <div class="mention-info">
                    <div class="mention-name">{{ friend.nickname || friend.username }}</div>
                    <div v-if="friend.school" class="mention-school">{{ friend.school }}</div>
                  </div>
                </div>
                <div v-if="filteredFriends.length === 0" class="mention-empty">
                  没有找到好友
                </div>
              </div>
            </div>
          </div>

          <!-- 话题输入区 -->
          <div class="form-item topic-section">
            <label class="form-label">
              <span class="label-icon">🏷️</span>
              添加话题（可选）
            </label>
            <div class="topic-input-wrapper">
              <div class="topic-input-container">
                <div class="topic-tags-display">
                  <span
                    v-for="(topic, index) in postForm.selectedTopics"
                    :key="index"
                    class="topic-tag-display"
                  >
                    #{{ topic }}
                    <button
                      class="tag-remove-btn"
                      @click="removeTopic(index)"
                    >
                      ✕
                    </button>
                  </span>
                  <input
                    v-model="topicInput"
                    type="text"
                    class="topic-input"
                    placeholder="输入话题名称，按回车添加"
                    @keyup.enter="addTopicFromInput"
                    @blur="addTopicFromInput"
                  />
                </div>
              </div>
              <div class="preset-topics">
                <div class="preset-topics-label">推荐话题：</div>
                <div class="preset-topics-buttons">
                  <button
                    v-for="topic in presetTopics"
                    :key="topic"
                    class="preset-topic-btn"
                    :class="{ active: isTopicSelected(topic) }"
                    @click="togglePresetTopic(topic)"
                  >
                    #{{ topic }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 媒体上传区 -->
          <div class="form-item media-section">
            <label class="form-label">
              <span class="label-icon">📸</span>
              添加媒体（图片和视频可同时添加）
            </label>

            <!-- 图片上传 -->
            <div class="media-upload-group">
              <div class="upload-group-header">
                <span class="group-icon">📷</span>
                <span class="group-title">图片</span>
                <span v-if="postForm.images.length > 0" class="group-badge">{{ postForm.images.length }}张</span>
              </div>
              <div class="image-upload-area">
                <input
                  ref="imageInput"
                  type="file"
                  accept="image/*"
                  multiple
                  class="image-input"
                  @change="handleImageSelect"
                />
                <button class="upload-btn upload-btn-image" @click="triggerImageUpload">
                  <span class="btn-icon">📷</span>
                  <span>选择图片（最多9张）</span>
                </button>
                <p class="upload-hint">支持 JPG、PNG 格式，单张不超过 5MB</p>
              </div>
            </div>

            <!-- 视频上传 -->
            <div class="media-upload-group">
              <div class="upload-group-header">
                <span class="group-icon">🎥</span>
                <span class="group-title">视频</span>
                <span v-if="postForm.video" class="group-badge">已添加</span>
              </div>
              <div class="video-upload-area">
                <input
                  ref="videoInput"
                  type="file"
                  accept="video/*"
                  class="video-input"
                  @change="handleVideoSelect"
                />
                <button class="upload-btn upload-btn-video" @click="triggerVideoUpload">
                  <span class="btn-icon">🎥</span>
                  <span>选择视频</span>
                </button>
                <p class="upload-hint">支持 MP4、MOV 格式，不超过 100MB</p>
              </div>
            </div>
          </div>

          <!-- 关联场馆 -->
          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">📍</span>
              关联场馆（可选）
            </label>
            <select v-model="postForm.venueId" class="form-select venue-select">
              <option :value="0">不选择场馆</option>
              <option
                v-for="venue in venues"
                :key="venue.id"
                :value="venue.id"
              >
                {{ venue.name }}
              </option>
            </select>
          </div>

          <!-- 预览区域 -->
          <div v-if="postForm.images.length > 0 || postForm.video" class="form-item preview-section">
            <label class="form-label">
              <span class="label-icon">👁️</span>
              预览
            </label>
            <div class="preview-content">
              <div v-if="postForm.images.length > 0" class="image-preview-list">
                <div
                  v-for="(img, index) in postForm.images"
                  :key="index"
                  class="image-preview-item"
                >
                  <img :src="getImageUrl(img)" alt="预览" class="preview-image" />
                  <button class="remove-image-btn" @click="removeImage(index)">
                    ✕
                  </button>
                </div>
              </div>
              <div v-if="postForm.video" class="video-preview">
                <video
                  :src="getVideoUrl(postForm.video)"
                  controls
                  class="preview-video"
                ></video>
                <button class="remove-video-btn" @click="removeVideo">
                  ✕ 删除视频
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="footer-tips">
            <span class="tip-item">💡 添加话题让更多人看到</span>
            <span class="tip-item">📸 可以同时添加图片和视频</span>
          </div>
          <div class="footer-actions">
            <BaseButton type="secondary" @click="closeModal">取消</BaseButton>
            <BaseButton
              type="primary"
              :disabled="!canPublish"
              @click="handlePublish"
            >
              {{ (postForm as any).editingPostId ? '保存修改' : '发布动态' }}
            </BaseButton>
          </div>
        </div>
      </div>
    </div>

    <!-- 分享动态到好友 -->
    <div v-if="showShareModal" class="modal-overlay" @click.self="showShareModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title">分享给好友</h3>
          <button class="modal-close" @click="showShareModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div v-if="friends.length === 0" class="empty-state">
            <p>还没有好友，先去添加好友吧～</p>
          </div>
          <div v-else class="friends-select-list">
            <label
              v-for="friend in friends"
              :key="friend.id"
              class="friend-option"
            >
              <input
                type="radio"
                :value="friend.id"
                v-model.number="selectedFriendId"
              />
              <div class="friend-info">
                <div class="friend-avatar">
                  <img
                    v-if="friend.avatar"
                    :src="getImageUrl(friend.avatar)"
                    alt="avatar"
                  />
                  <span v-else>{{ (friend.nickname || friend.username)[0] }}</span>
                </div>
                <div class="friend-text">
                  <div class="friend-name">{{ friend.nickname || friend.username }}</div>
                  <div class="friend-school" v-if="friend.school">{{ friend.school }}</div>
                </div>
              </div>
            </label>
          </div>
          <div v-if="shareContentPreview" class="share-preview">
            <div class="share-preview-title">分享内容</div>
            <div class="share-preview-text">{{ shareContentPreview }}</div>
            <img
              v-if="shareCardPayload?.image"
              :src="getImageUrl(shareCardPayload.image)"
              alt="封面"
              class="share-preview-thumb"
            />
          </div>
        </div>
        <div class="modal-footer">
          <BaseButton type="secondary" @click="showShareModal = false">取消</BaseButton>
          <BaseButton :loading="shareSending" type="primary" @click="confirmShare">发送</BaseButton>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <div v-if="previewImageUrl" class="image-preview-overlay" @click="previewImageUrl = ''">
      <div class="image-preview-content" @click.stop>
        <button class="image-preview-close" @click="previewImageUrl = ''">✕</button>
        <img :src="previewImageUrl" alt="预览图片" class="image-preview-img" />
      </div>
    </div>

    <!-- 用户信息弹窗 -->
    <div v-if="showUserInfoModal" class="modal-overlay" @click.self="showUserInfoModal = false">
      <div class="modal-content user-info-modal">
        <div class="modal-header user-info-header">
          <div class="user-info-header-text">
            <div class="user-info-header-sub">查看动态发布者</div>
            <h3 class="modal-title user-info-header-title">用户信息</h3>
          </div>
          <button class="modal-close" @click="showUserInfoModal = false">✕</button>
        </div>
        <div class="modal-body user-info-body">
          <div v-if="userInfoLoading" class="loading">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>
          <div v-else-if="selectedUser" class="user-info-content">
            <div class="user-info-avatar-wrapper">
              <div class="user-info-avatar-ring">
                <div class="user-info-avatar-glow"></div>
                <div class="user-info-avatar">
                  <img
                    v-if="selectedUser.avatar"
                    :src="getImageUrl(selectedUser.avatar)"
                    alt="头像"
                    class="avatar-img"
                  />
                  <div v-else class="avatar-placeholder-large">
                    {{ (selectedUser.nickname || selectedUser.username)?.[0]?.toUpperCase() || '👤' }}
                  </div>
                </div>
              </div>
              <div class="user-info-badge-row">
                <span class="user-info-badge">
                  🏃 运动爱好者
                </span>
                <span v-if="isFriend" class="user-info-badge user-info-badge--success">
                  ✓ 已是好友
                </span>
              </div>
            </div>
            <div class="user-info-details">
              <div class="user-info-name">{{ selectedUser.nickname || selectedUser.username }}</div>
              <div class="user-info-meta-row">
                <div v-if="selectedUser.username" class="user-info-username">
                  @{{ selectedUser.username }}
                </div>
                <div
                  v-if="selectedUser.school"
                  class="user-info-school"
                  title="所在学校"
                >
                  🏫 {{ selectedUser.school }}
                </div>
              </div>
              <div v-if="selectedUser.bio" class="user-info-bio">
                {{ selectedUser.bio }}
              </div>
              <div v-if="selectedUser.email" class="user-info-email">
                📧 {{ selectedUser.email }}
              </div>
            </div>
            <div class="user-info-divider"></div>
            <div class="user-info-actions">
              <BaseButton
                v-if="!isCurrentUser && !isFriend"
                type="primary"
                size="large"
                @click="handleAddFriend"
              >
                ➕ 添加好友
              </BaseButton>
              <BaseButton
                v-else-if="isFriend"
                type="secondary"
                size="large"
                disabled
              >
                ✓ 已是好友
              </BaseButton>
              <BaseButton
                v-if="!isCurrentUser"
                type="secondary"
                size="large"
                @click="handleStartChat"
              >
                💬 发送消息
              </BaseButton>
            </div>
            <div class="user-info-footer-hint" v-if="!isCurrentUser">
              <span>小提示：添加好友后可以在「好友聊天」中和 TA 私聊</span>
            </div>
          </div>
          <div v-else class="empty-state user-not-found">
            <div class="empty-icon-large">👤</div>
            <h3 class="empty-title">未找到该用户</h3>
            <p class="empty-description">
              抱歉，没有找到用户 "<strong>@{{ searchedUsername }}</strong>"
            </p>
            <div class="empty-suggestions">
              <p class="suggestions-title">可能的原因：</p>
              <ul class="suggestions-list">
                <li>该用户可能已注销账号</li>
                <li>用户名或昵称可能已更改</li>
                <li>该用户可能不在当前系统中</li>
              </ul>
            </div>
            <BaseButton type="secondary" @click="showUserInfoModal = false" class="empty-close-btn">
              关闭
            </BaseButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useSocialStore } from '@/stores/social'
import { useVenueStore } from '@/stores/venue'
import { useUserStore } from '@/stores/user'
import { useFriendsStore } from '@/stores/friends'
import { useChatStore } from '@/stores/chat'
import { useTopicStore } from '@/stores/topic'
import { BaseButton, BaseSelect } from '@/components/common'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const socialStore = useSocialStore()
const venueStore = useVenueStore()
const userStore = useUserStore()
const friendsStore = useFriendsStore()
const chatStore = useChatStore()
const topicStore = useTopicStore()

const posts = computed(() => socialStore.posts)
const loading = computed(() => socialStore.loading)
const venues = computed(() => venueStore.venues)
const userInfo = computed(() => userStore.userInfo)
const friends = computed(() => friendsStore.friends)
const topics = computed(() => topicStore.topics)

const showPublishModal = ref(false)
const showMyPosts = ref(false)
const imageInput = ref<HTMLInputElement | null>(null)
const videoInput = ref<HTMLInputElement | null>(null)
const contentTextarea = ref<HTMLTextAreaElement | null>(null)
const showComments = reactive<Record<number, boolean>>({})
const commentInputs = reactive<Record<number, string>>({})
const replyingTo = reactive<Record<number, string>>({}) // 记录正在回复的用户名
const replyingToCommentId = reactive<Record<number, number>>({}) // 记录正在回复的评论ID
const previewImageUrl = ref('')
const previewVideoUrl = ref('')
const highlightPostId = ref<number | null>(null)

// @好友相关
const showMentionList = ref(false)
const mentionKeyword = ref('')
const selectedMentionIndex = ref(0)
const mentionStartPos = ref(-1) // @符号的位置

// 用户信息弹窗相关
const showUserInfoModal = ref(false)
const selectedUser = ref<any>(null)
const userInfoLoading = ref(false)
const searchedUsername = ref<string>('') // 记录搜索的用户名

// 排序选项
const sortOptions = [
  { value: 'latest', label: '最新', icon: '🕐' },
  { value: 'hot', label: '热门', icon: '🔥' },
  { value: 'featured', label: '精华', icon: '⭐' },
]
const currentSort = ref('latest')
const selectedTopicId = ref<number>(0)

const postForm = reactive({
  content: '',
  images: [] as string[], // 存储文件路径
  imageFiles: [] as File[], // 存储原始文件对象
  video: '' as string, // 存储视频路径
  videoFile: null as File | null, // 存储原始视频文件对象
  venueId: 0,
  selectedTopics: [] as string[], // 选中的话题列表
  mentionedUserIds: [] as number[], // 被@的好友ID列表
})

// 话题输入框
const topicInput = ref('')

// 预设话题列表（健身相关）
const presetTopics = [
  '健身打卡',
  '跑步',
  '瑜伽',
  '力量训练',
  '有氧运动',
  '减脂',
  '增肌',
  '运动装备',
  '健身餐',
  '运动心得',
  '晨练',
  '夜跑',
  '游泳',
  '篮球',
  '羽毛球',
  '乒乓球',
  '舞蹈',
  '骑行',
  '登山',
  '运动日常',
]

// 字符计数
const charCount = computed(() => postForm.content.length)

// 是否可以发布
const canPublish = computed(() => {
  return postForm.content.trim().length > 0 || postForm.images.length > 0 || postForm.video
})

// 分享到好友
const showShareModal = ref(false)
const shareTargetPost = ref<any | null>(null)
const selectedFriendId = ref<number | null>(null)
const shareSending = ref(false)

// 我的动态
const myPosts = computed(() => {
  const currentUserId = userInfo.value?.id || 1
  return posts.value.filter((post) => post.userId === currentUserId)
})

const myPostsCount = computed(() => myPosts.value.length)

// 计算总点赞数
const totalLikes = computed(() => {
  return myPosts.value.reduce((sum, post) => sum + post.likes, 0)
})

const currentUserId = computed(() => userInfo.value?.id || 1)

const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

// 格式化内容，将话题标签转换为可点击的链接（@好友已单独显示为标签）
const formatContentWithTopics = (content: string) => {
  if (!content) return ''

  // 先转义HTML，避免XSS攻击
  const escapeHtml = (text: string) => {
    const map: Record<string, string> = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;'
    }
    return text.replace(/[&<>"']/g, (m) => map[m])
  }

  // 转义整个内容
  let formatted = escapeHtml(content)

  // 移除@好友标记（因为会单独显示为标签）
  formatted = formatted.replace(/@([^\s@]+?)(?=\s|$|[，。！？；：、\n\r])/g, '')

  // 处理话题标签，格式：#话题名
  formatted = formatted.replace(/#([^\s#]+)/g, (match, topicName) => {
    const encodedTopic = encodeURIComponent(topicName)
    return `<a href="/topics/${encodedTopic}" class="topic-link">${match}</a>`
  })

  return formatted
}

// 从动态内容中提取@的好友名列表
const getMentionedUsers = (post: any): string[] => {
  if (!post.content) return []
  const mentions: string[] = []
  const matches = post.content.matchAll(/@([^\s@]+?)(?=\s|$|[，。！？；：、\n\r])/g)
  for (const match of matches) {
    const mention = match[1]
    if (mention && !mentions.includes(mention)) {
      mentions.push(mention)
    }
  }
  return mentions
}

// 处理@好友标签点击
const handleMentionClick = async (mention: string) => {
  await showUserInfo(mention)
}

// 处理内容区域点击事件（现在主要用于话题链接）
const handleContentClick = async (event: MouseEvent, post: any) => {
  // @好友现在单独显示为标签，点击事件在handleMentionClick中处理
}

// 显示用户信息
const showUserInfo = async (username: string) => {
  userInfoLoading.value = true
  showUserInfoModal.value = true
  selectedUser.value = null
  searchedUsername.value = username // 记录搜索的用户名

  try {
    // 先尝试从好友列表中精确查找（最快）
    const friend = friends.value.find((f: any) =>
      f.username === username || f.nickname === username
    )
    if (friend) {
      selectedUser.value = friend
      userInfoLoading.value = false
      return
    }

    // 如果好友列表中没有，通过API搜索用户
    const users = await friendsStore.searchUsers(username)

    // 优先精确匹配用户名或昵称
    let user = users.find((u: any) =>
      u.username === username || u.nickname === username
    )

    // 如果精确匹配不到，尝试模糊匹配（用户名或昵称包含搜索关键词）
    if (!user && users.length > 0) {
      user = users.find((u: any) =>
        (u.username && u.username.includes(username)) ||
        (u.nickname && u.nickname.includes(username))
      )
    }

    // 如果还是找不到，取第一个搜索结果（可能是最相关的）
    if (!user && users.length > 0) {
      user = users[0]
    }

    if (user) {
      selectedUser.value = user
    }
    // 如果仍然找不到，selectedUser.value 保持为 null，会显示"用户不存在"提示
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 出错时也保持 selectedUser.value 为 null
  } finally {
    userInfoLoading.value = false
  }
}

// 判断是否是当前用户
const isCurrentUser = computed(() => {
  return selectedUser.value && userInfo.value && selectedUser.value.id === userInfo.value.id
})

// 判断是否已是好友
const isFriend = computed(() => {
  if (!selectedUser.value) return false
  return friends.value.some((f: any) => f.id === selectedUser.value.id)
})

// 添加好友
const handleAddFriend = async () => {
  if (!selectedUser.value) return

  try {
    await friendsStore.sendFriendRequest(selectedUser.value.id, `你好，我是${userInfo.value?.nickname || userInfo.value?.username || '用户'}，想添加你为好友`)
    alert('好友请求已发送！')
    showUserInfoModal.value = false
  } catch (error: any) {
    alert(error.message || '发送好友请求失败，请稍后重试')
  }
}

// 开始聊天
const handleStartChat = async () => {
  if (!selectedUser.value) return

  try {
    const chat = await chatStore.createChat(
      selectedUser.value.id,
      selectedUser.value.nickname || selectedUser.value.username
    )
    showUserInfoModal.value = false
    // 跳转到好友聊天页面，并携带 userId 参数
    router.push({
      name: 'chat-user',
      params: { userId: selectedUser.value.id },
    })
  } catch (error) {
    console.error('创建聊天失败:', error)
    alert('创建聊天失败，请稍后重试')
  }
}

const getComments = (postId: number) => {
  return socialStore.comments.filter((c) => c.postId === postId)
}

const previewImage = (url: string) => {
  previewImageUrl.value = url
}

const triggerImageUpload = () => {
  imageInput.value?.click()
}

const triggerVideoUpload = () => {
  videoInput.value?.click()
}

const handleImageSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) return

  // 验证所有文件
  const validFiles: File[] = []
  for (const file of Array.from(files)) {
    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      alert(`${file.name} 不是图片文件`)
      continue
    }

    // 验证文件大小（限制为5MB）
    if (file.size > 5 * 1024 * 1024) {
      alert(`${file.name} 大小不能超过5MB`)
      continue
    }

    validFiles.push(file)
  }

  if (validFiles.length === 0) return

  // 上传文件（不再清空视频，允许同时存在）
  try {
    const formData = new FormData()
    validFiles.forEach((file) => {
      formData.append('files', file)
    })

    const response = await request.post<{ paths: string[]; urls: string[] }>('/upload/social', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    // 保存文件路径和预览URL
    response.paths.forEach((path, index) => {
      if (!postForm.images.includes(path)) {
        postForm.images.push(path)
        postForm.imageFiles.push(validFiles[index])
      }
    })

    // 显示预览（使用URL）
    // 注意：这里我们存储路径，显示时使用URL
  } catch (error) {
    alert('图片上传失败，请稍后重试')
    console.error('上传失败:', error)
  }

  // 清空input，允许重复选择同一文件
  if (imageInput.value) {
    imageInput.value.value = ''
  }
}

const removeImage = (index: number) => {
  postForm.images.splice(index, 1)
  postForm.imageFiles.splice(index, 1)
}

const handleVideoSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('video/')) {
    alert(`${file.name} 不是视频文件`)
    return
  }

  // 验证文件大小（限制为100MB）
  if (file.size > 100 * 1024 * 1024) {
    alert(`${file.name} 大小不能超过100MB`)
    return
  }

  // 上传视频
  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await request.post<{ path: string; url: string }>('/upload/social/video', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    postForm.video = response.path
    postForm.videoFile = file
    // 允许图片和视频同时存在，不再清空图片
  } catch (error) {
    alert('视频上传失败，请稍后重试')
    console.error('上传失败:', error)
  }

  // 清空input，允许重复选择同一文件
  if (videoInput.value) {
    videoInput.value.value = ''
  }
}

const removeVideo = () => {
  postForm.video = ''
  postForm.videoFile = null
  if (videoInput.value) {
    videoInput.value.value = ''
  }
}

const getVideoUrl = (path: string) => {
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

// 获取图片URL（用于显示）
const getImageUrl = (path: string) => {
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  return `${baseURL.replace('/api', '')}/api/files/${path}`
}

// 从输入框添加话题
const addTopicFromInput = () => {
  const topic = topicInput.value.trim()
  if (topic) {
    // 移除#号（如果有）
    const cleanTopic = topic.startsWith('#') ? topic.substring(1) : topic
    if (cleanTopic && !postForm.selectedTopics.includes(cleanTopic)) {
      postForm.selectedTopics.push(cleanTopic)
    }
    topicInput.value = ''
  }
}

// 移除话题
const removeTopic = (index: number) => {
  postForm.selectedTopics.splice(index, 1)
}

// 切换预设话题
const togglePresetTopic = (topic: string) => {
  const index = postForm.selectedTopics.indexOf(topic)
  if (index > -1) {
    postForm.selectedTopics.splice(index, 1)
  } else {
    postForm.selectedTopics.push(topic)
  }
}

// 检查话题是否已选中
const isTopicSelected = (topic: string) => {
  return postForm.selectedTopics.includes(topic)
}

// 过滤好友列表（用于@功能）
const filteredFriends = computed(() => {
  if (!mentionKeyword.value) {
    return friends.value
  }
  const keyword = mentionKeyword.value.toLowerCase()
  return friends.value.filter((friend) => {
    const name = (friend.nickname || friend.username || '').toLowerCase()
    const school = (friend.school || '').toLowerCase()
    return name.includes(keyword) || school.includes(keyword)
  })
})

// 处理内容输入
const handleContentInput = (event: Event) => {
  const textarea = event.target as HTMLTextAreaElement
  const content = textarea.value
  const cursorPos = textarea.selectionStart

  // 查找最后一个@符号
  const lastAtIndex = content.lastIndexOf('@', cursorPos - 1)

  if (lastAtIndex !== -1) {
    // 检查@后面是否有空格或其他分隔符
    const afterAt = content.substring(lastAtIndex + 1, cursorPos)
    if (!afterAt.match(/[\s\n]/)) {
      // 显示好友列表
      mentionStartPos.value = lastAtIndex
      mentionKeyword.value = afterAt
      showMentionList.value = true
      selectedMentionIndex.value = 0
    } else {
      showMentionList.value = false
    }
  } else {
    showMentionList.value = false
  }
}

// 处理键盘事件
const handleContentKeydown = (event: KeyboardEvent) => {
  if (showMentionList.value) {
    if (event.key === 'ArrowDown') {
      event.preventDefault()
      selectedMentionIndex.value = Math.min(selectedMentionIndex.value + 1, filteredFriends.value.length - 1)
    } else if (event.key === 'ArrowUp') {
      event.preventDefault()
      selectedMentionIndex.value = Math.max(selectedMentionIndex.value - 1, 0)
    } else if (event.key === 'Enter' && filteredFriends.value.length > 0) {
      event.preventDefault()
      selectMention(filteredFriends.value[selectedMentionIndex.value])
    } else if (event.key === 'Escape') {
      showMentionList.value = false
    }
  }
}

// 选择好友
const selectMention = (friend: any) => {
  if (!contentTextarea.value) return

  const content = postForm.content
  const beforeAt = content.substring(0, mentionStartPos.value)
  const afterCursor = content.substring(contentTextarea.value.selectionStart)

  // 插入@好友名
  const friendName = friend.nickname || friend.username
  const newContent = beforeAt + '@' + friendName + ' ' + afterCursor

  postForm.content = newContent

  // 添加好友ID到列表（如果还没有）
  if (!postForm.mentionedUserIds.includes(friend.id)) {
    postForm.mentionedUserIds.push(friend.id)
  }

  // 关闭好友列表
  showMentionList.value = false
  mentionKeyword.value = ''

  // 设置光标位置
  setTimeout(() => {
    if (contentTextarea.value) {
      const newCursorPos = mentionStartPos.value + friendName.length + 2 // @ + 名字 + 空格
      contentTextarea.value.setSelectionRange(newCursorPos, newCursorPos)
      contentTextarea.value.focus()
    }
  }, 0)
}

const closeModal = () => {
  showPublishModal.value = false
  // 重置表单
  postForm.content = ''
  postForm.images = []
  postForm.imageFiles = []
  postForm.video = ''
  postForm.videoFile = null
  postForm.venueId = 0
  postForm.selectedTopics = []
  postForm.mentionedUserIds = []
  topicInput.value = ''
  showMentionList.value = false
  mentionKeyword.value = ''
  ;(postForm as any).editingPostId = null
}

const handlePublish = async () => {
  if (!canPublish.value) {
    alert('请输入动态内容或添加图片/视频')
    return
  }

  const selectedVenue = venues.value.find((v) => v.id === postForm.venueId)
  const editingPostId = (postForm as any).editingPostId

  // 只使用选中的话题，不再从内容中提取
  const allTopics = postForm.selectedTopics && postForm.selectedTopics.length > 0 ? postForm.selectedTopics : undefined

  if (editingPostId) {
    // 编辑模式
    await socialStore.updatePost(editingPostId, {
      content: postForm.content,
      images: postForm.images.length > 0 ? postForm.images : undefined,
      video: postForm.video || undefined,
      venueId: postForm.venueId > 0 ? postForm.venueId : undefined,
      venueName: selectedVenue?.name,
      topics: allTopics,
      mentionedUserIds: postForm.mentionedUserIds && postForm.mentionedUserIds.length > 0 ? postForm.mentionedUserIds : undefined,
    })
    alert('更新成功！')
  } else {
    // 新建模式
    await socialStore.createPost({
      content: postForm.content,
      images: postForm.images.length > 0 ? postForm.images : undefined,
      video: postForm.video || undefined,
      venueId: postForm.venueId > 0 ? postForm.venueId : undefined,
      venueName: selectedVenue?.name,
      topics: allTopics,
      mentionedUserIds: postForm.mentionedUserIds && postForm.mentionedUserIds.length > 0 ? postForm.mentionedUserIds : undefined,
      userId: userInfo.value?.id,
      username: userInfo.value?.nickname || userInfo.value?.username || '我',
      avatar: userInfo.value?.avatar,
    })
    alert('发布成功！')
  }

  closeModal()
}

const handleEditPost = (post: any) => {
  // 填充表单数据
  postForm.content = post.content
  postForm.images = post.images || [] // 这里已经是路径了
  postForm.imageFiles = [] // 编辑时不需要重新上传
  postForm.video = post.video || '' // 视频路径
  postForm.videoFile = null // 编辑时不需要重新上传
  postForm.venueId = post.venueId || 0
  // 加载已有话题
  if (post.topics && post.topics.length > 0) {
    postForm.selectedTopics = [...post.topics]
  } else {
    postForm.selectedTopics = []
  }
  // 加载已@的好友
  if (post.mentionedUserIds && post.mentionedUserIds.length > 0) {
    postForm.mentionedUserIds = [...post.mentionedUserIds]
  } else {
    postForm.mentionedUserIds = []
  }
  topicInput.value = ''
  showMentionList.value = false
  mentionKeyword.value = ''
  showPublishModal.value = true
  // 标记为编辑模式
  ;(postForm as any).editingPostId = post.id
}

const handleDeletePost = async (postId: number) => {
  if (confirm('确定要删除这条动态吗？')) {
    await socialStore.deletePost(postId)
    alert('删除成功！')
  }
}

const handleLike = async (postId: number) => {
  await socialStore.toggleLike(postId)
}

const openShare = (post: any) => {
  if (!userInfo.value?.id) {
    alert('请先登录')
    router.push('/login')
    return
  }
  shareTargetPost.value = post
  selectedFriendId.value = null
  showShareModal.value = true
}

const shareCardPayload = computed(() => {
  if (!shareTargetPost.value) return null
  const content = shareTargetPost.value.content || ''
  const snippet = content.length > 80 ? `${content.slice(0, 80)}...` : content
  return {
    type: 'share_post',
    postId: shareTargetPost.value.id,
    author: shareTargetPost.value.username,
    authorAvatar: shareTargetPost.value.avatar,
    createdAt: shareTargetPost.value.createdAt,
    content: snippet,
    image: shareTargetPost.value.images?.[0] || '',
    hasVideo: !!shareTargetPost.value.video,
  }
})

const shareContentPreview = computed(() => {
  if (!shareCardPayload.value) return ''
  return `【动态分享】${userInfo.value?.nickname || userInfo.value?.username || '我'} 分享了一条动态给你：${shareCardPayload.value.content}`
})

const confirmShare = async () => {
  if (!shareTargetPost.value) return
  if (!selectedFriendId.value) {
    alert('请选择要分享的好友')
    return
  }
  const targetFriend =
    friends.value.find((f) => f.id === selectedFriendId.value) || null
  if (!targetFriend) {
    alert('好友信息不存在，请刷新后重试')
    return
  }
  if (!userInfo.value?.id) {
    alert('请先登录')
    router.push('/login')
    return
  }
  shareSending.value = true
  try {
    const chat = await chatStore.createChat(
      targetFriend.id,
      targetFriend.nickname || targetFriend.username,
    )
    const payload = shareCardPayload.value
    if (!payload) return
    await chatStore.sendMessage(
      chat.id,
      targetFriend.id,
      JSON.stringify(payload),
      'share_post',
    )
    alert('已通过私信分享给好友')
    showShareModal.value = false
  } catch (error) {
    console.error('分享失败', error)
    alert('分享失败，请稍后再试')
  } finally {
    shareSending.value = false
  }
}

const toggleComments = async (postId: number) => {
  showComments[postId] = !showComments[postId]
  if (showComments[postId]) {
    if (!commentInputs[postId]) {
      commentInputs[postId] = ''
    }
    // 加载评论列表
    await socialStore.fetchComments(postId)
  }
}

const handleAddComment = async (postId: number) => {
  const content = commentInputs[postId]?.trim()
  if (!content) {
    alert('请输入评论内容')
    return
  }
  const parentCommentId = replyingToCommentId[postId] || undefined
  await socialStore.addComment(postId, content, parentCommentId)
  commentInputs[postId] = ''
  delete replyingTo[postId]
  delete replyingToCommentId[postId]
  // 重新加载评论列表以显示最新回复
  await socialStore.fetchComments(postId)
}

const handleReply = (postId: number, commentId: number, username: string) => {
  replyingTo[postId] = username
  replyingToCommentId[postId] = commentId
  // 聚焦到输入框
  setTimeout(() => {
    const input = document.querySelector(`input[placeholder*="${username}"]`) as HTMLInputElement
    input?.focus()
  }, 100)
}

const cancelReply = (postId: number) => {
  delete replyingTo[postId]
  delete replyingToCommentId[postId]
  commentInputs[postId] = ''
}

// 切换排序
const changeSort = (sortBy: string) => {
  currentSort.value = sortBy
  const topicId = selectedTopicId.value > 0 ? selectedTopicId.value : undefined
  socialStore.fetchPosts(topicId, sortBy)
}

// 话题筛选变化
const handleTopicChange = (value: string | number) => {
  const topicId = typeof value === 'string' ? Number(value) : value
  selectedTopicId.value = topicId
  const actualTopicId = topicId > 0 ? topicId : undefined
  socialStore.fetchPosts(actualTopicId, currentSort.value)
}

// 清除话题筛选
const clearTopicFilter = () => {
  selectedTopicId.value = 0
  socialStore.fetchPosts(undefined, currentSort.value)
}

const scrollToPost = async (postId: number) => {
  await nextTick()
  const el = document.querySelector<HTMLElement>(`#post-${postId}`)
  if (!el) return
  el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  highlightPostId.value = postId
  window.setTimeout(() => {
    if (highlightPostId.value === postId) {
      highlightPostId.value = null
    }
  }, 3000)
}

// 点击外部关闭@好友列表
const handleClickOutside = (event: MouseEvent) => {
  if (!showMentionList.value) return

  const target = event.target as HTMLElement
  const textarea = contentTextarea.value
  const mentionList = document.querySelector('.mention-list')

  // 如果点击的不是textarea和mention-list，则关闭
  if (textarea && mentionList) {
    if (!textarea.contains(target) && !mentionList.contains(target)) {
      showMentionList.value = false
    }
  }
}

onMounted(async () => {
  await socialStore.fetchPosts(undefined, currentSort.value)
  venueStore.fetchVenues()
  friendsStore.fetchFriends()
  topicStore.fetchTopics()
  // 添加点击外部关闭@好友列表的监听
  document.addEventListener('click', handleClickOutside)
  const postIdParam = route.query.postId
  if (postIdParam) {
    const targetId = Number(postIdParam)
    if (!Number.isNaN(targetId)) {
      scrollToPost(targetId)
    }
  }
})

onUnmounted(() => {
  // 移除点击外部关闭@好友列表的监听
  document.removeEventListener('click', handleClickOutside)
})

</script>

<style scoped>
.social-view {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: calc(100vh - 200px);
}

.social-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: var(--spacing-xl);
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-md);
}

.social-sidebar {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.user-profile-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: var(--spacing-xl);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: var(--spacing-lg);
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: transform 0.3s, box-shadow 0.3s;
}

.user-profile-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

.profile-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto var(--spacing-md);
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--border-lighter);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
}

.profile-info {
  text-align: center;
  margin-bottom: var(--spacing-md);
}

.profile-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
}

.profile-school {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xs);
}

.profile-bio {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin-top: var(--spacing-sm);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.profile-stats {
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-lighter);
}

.stat-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
}

.stat-icon {
  font-size: 16px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
}


.my-posts-section {
  margin-bottom: var(--spacing-md);
}

.my-posts-section .base-button {
  width: 100%;
}

.post-creator {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: var(--spacing-lg);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin-bottom: var(--spacing-md);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.post-creator .base-button {
  width: 100%;
  border-radius: 12px;
  font-weight: 600;
  padding: 14px;
}

.my-posts-list {
  background-color: var(--bg-color);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  margin-top: var(--spacing-md);
  max-height: 400px;
  overflow-y: auto;
}

.empty-posts {
  text-align: center;
  padding: var(--spacing-lg);
  color: var(--text-secondary);
  font-size: 13px;
}

.posts-mini-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.mini-post-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  background-color: var(--bg-color-page);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-lighter);
  transition: all 0.3s;
}

.mini-post-item:hover {
  border-color: var(--primary-color);
  background-color: var(--border-extra-light);
}

.mini-post-content {
  flex: 1;
  min-width: 0;
}

.mini-post-text {
  font-size: 13px;
  color: var(--text-regular);
  line-height: 1.5;
  margin-bottom: var(--spacing-xs);
  word-break: break-word;
}

.mini-post-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 11px;
  color: var(--text-placeholder);
}

.mini-post-time {
  flex-shrink: 0;
}

.mini-post-stats {
  flex-shrink: 0;
}

.mini-post-actions {
  display: flex;
  gap: var(--spacing-xs);
  flex-shrink: 0;
}

.action-icon-btn {
  width: 28px;
  height: 28px;
  border: none;
  background-color: transparent;
  font-size: 14px;
  cursor: pointer;
  border-radius: var(--border-radius-sm);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon-btn:hover {
  background-color: var(--border-extra-light);
}

.action-icon-btn:last-child:hover {
  background-color: var(--danger-color);
  color: #fff;
}

.social-main {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: var(--spacing-xl);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.main-header {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.header-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.topic-filter {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
}

.filter-icon {
  font-size: 16px;
}

.topic-select {
  min-width: 200px;
  max-width: 300px;
}

.clear-filter-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(255, 71, 87, 0.1);
  color: var(--danger-color);
  border-radius: 50%;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  flex-shrink: 0;
}

.clear-filter-btn:hover {
  background: var(--danger-color);
  color: #fff;
  transform: scale(1.1);
}

.sort-controls {
  display: flex;
  gap: var(--spacing-xs);
  flex-wrap: wrap;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 2px solid var(--border-base);
  border-radius: 20px;
  background: #fff;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.sort-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.sort-btn.active {
  border-color: var(--primary-color);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: var(--primary-color);
  font-weight: 600;
}

.sort-icon {
  font-size: 16px;
}

.loading {
  text-align: center;
  padding: var(--spacing-xxl);
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(64, 158, 255, 0.1);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state {
  text-align: center;
  padding: var(--spacing-xxl);
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: var(--spacing-md);
  opacity: 0.5;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.post-card {
  position: relative;
  padding: var(--spacing-xl);
  border: 2px solid rgba(0, 0, 0, 0.06);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.98);
  transition: all 0.3s;
  animation: fadeIn 0.5s ease-out;
  margin-bottom: var(--spacing-lg);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.post-card-highlight {
  border-color: rgba(64, 158, 255, 0.9);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.4);
  animation: highlightPulse 0.9s ease-out 1;
}

@keyframes highlightPulse {
  0% {
    transform: scale(1.005);
  }
  100% {
    transform: scale(1);
  }
}

.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, transparent 0%, var(--primary-color) 50%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.post-card:hover {
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.3);
  transform: translateY(-2px);
}

.post-card:hover::before {
  opacity: 1;
}

.post-card.post-pinned {
  border-color: rgba(255, 193, 7, 0.4);
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.02) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.post-card.post-pinned::before {
  background: linear-gradient(90deg, transparent 0%, #ffc107 50%, transparent 100%);
  opacity: 1;
}

.post-card.post-featured {
  border-color: rgba(255, 152, 0, 0.4);
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.02) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.post-card.post-featured::before {
  background: linear-gradient(90deg, transparent 0%, #ff9800 50%, transparent 100%);
  opacity: 1;
}

.post-card.post-hot {
  border-color: rgba(255, 87, 34, 0.4);
  background: linear-gradient(135deg, rgba(255, 87, 34, 0.02) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.post-card.post-hot::before {
  background: linear-gradient(90deg, transparent 0%, #ff5722 50%, transparent 100%);
  opacity: 1;
}

.post-badges {
  display: flex;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-md);
  flex-wrap: wrap;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.badge-pinned {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 193, 7, 0.3);
  animation: pulse-badge 2s ease-in-out infinite;
}

.badge-hot {
  background: linear-gradient(135deg, #ff5722 0%, #e64a19 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 87, 34, 0.3);
  animation: pulse-badge 2s ease-in-out infinite;
  animation-delay: 1s;
}

.badge-featured {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
  animation: pulse-badge 2s ease-in-out infinite;
  animation-delay: 0.5s;
}

.badge-hot {
  background: linear-gradient(135deg, #ff5722 0%, #e64a19 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 87, 34, 0.3);
  animation: pulse-badge 2s ease-in-out infinite;
  animation-delay: 1s;
}

@keyframes pulse-badge {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
}

.post-author {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  overflow: hidden;
  flex-shrink: 0;
}

.author-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-menu {
  display: flex;
  gap: var(--spacing-xs);
}

.menu-btn {
  width: 32px;
  height: 32px;
  border: none;
  background-color: transparent;
  font-size: 16px;
  cursor: pointer;
  border-radius: var(--border-radius-sm);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-btn:hover {
  background-color: var(--border-extra-light);
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 15px;
}

.post-time {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.post-content {
  margin-bottom: var(--spacing-md);
  position: relative;
}

.post-content p {
  color: var(--text-regular);
  line-height: 1.8;
  margin-bottom: var(--spacing-sm);
  font-size: 15px;
  word-break: break-word;
}

.post-content .topic-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
}

.post-content .topic-link:hover {
  text-decoration: underline;
  opacity: 0.8;
}


.post-mentions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.mention-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(64, 158, 255, 0.15) 100%);
  color: var(--primary-color);
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s;
  border: 1px solid rgba(64, 158, 255, 0.2);
  cursor: pointer;
}

.mention-tag:hover {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2) 0%, rgba(64, 158, 255, 0.25) 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.post-topics {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.topic-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(64, 158, 255, 0.15) 100%);
  color: var(--primary-color);
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.topic-tag:hover {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2) 0%, rgba(64, 158, 255, 0.25) 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.post-venue {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: var(--spacing-sm);
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
}

.post-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.post-image:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.post-video {
  margin-top: var(--spacing-md);
}

.post-video-player {
  width: 100%;
  max-height: 500px;
  border-radius: 16px;
  background-color: #000;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

/* 图片预览样式 */
.image-preview-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  cursor: zoom-out;
}

.image-preview-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
  cursor: default;
}

.image-preview-img {
  max-width: 100%;
  max-height: 80vh;
  border-radius: var(--border-radius-md);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.4);
}

.image-preview-close {
  position: absolute;
  top: -40px;
  right: 0;
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #ffffff;
  font-size: 22px;
  cursor: pointer;
}

.post-actions {
  display: flex;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-md);
  border-top: 2px solid rgba(0, 0, 0, 0.04);
  margin-top: var(--spacing-sm);
}

.action-btn {
  flex: 1;
  padding: 12px 18px;
  border: none;
  background: rgba(0, 0, 0, 0.02);
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s;
  position: relative;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  overflow: hidden;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.action-btn:hover::before {
  width: 300px;
  height: 300px;
}

.action-btn:hover {
  background: rgba(64, 158, 255, 0.08);
  color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.action-btn--active {
  color: #ff4757;
  background: rgba(255, 71, 87, 0.1);
  font-weight: 600;
}

.action-btn--active:hover {
  background: rgba(255, 71, 87, 0.15);
  color: #ff4757;
}

.action-btn--active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(245, 108, 108, 0.1);
  border-radius: var(--border-radius-md);
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 0.5;
  }
  50% {
    opacity: 0.8;
  }
}

.post-comments {
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-lighter);
}

.comment-form {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  align-items: center;
  flex-wrap: wrap;
}

.comment-input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.9);
}

.comment-input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: #fff;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.comment-item {
  padding: var(--spacing-md);
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.comment-item:hover {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xs);
}

.comment-author-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  flex: 1;
}

.comment-author {
  font-weight: 600;
  font-size: 13px;
  color: var(--text-primary);
}

.comment-reply-to {
  font-size: 12px;
  color: var(--text-secondary);
  font-style: italic;
}

.reply-btn {
  padding: 4px 12px;
  border: 1px solid var(--border-base);
  background: transparent;
  color: var(--text-secondary);
  font-size: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.reply-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(64, 158, 255, 0.05);
}

.comment-content {
  font-size: 14px;
  color: var(--text-regular);
  margin-bottom: var(--spacing-xs);
  line-height: 1.5;
}

.comment-footer {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.replies-list {
  margin-top: var(--spacing-md);
  padding-left: var(--spacing-lg);
  border-left: 2px solid rgba(64, 158, 255, 0.1);
}

.reply-item {
  padding: var(--spacing-sm);
  background: rgba(64, 158, 255, 0.02);
  border-radius: 8px;
  margin-bottom: var(--spacing-xs);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: var(--spacing-md);
  animation: fadeIn 0.3s ease-out;
}

.publish-modal {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: var(--spacing-xl);
  border-bottom: 2px solid rgba(64, 158, 255, 0.1);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05) 0%, rgba(255, 255, 255, 0.05) 100%);
}

.modal-header-left {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  flex: 1;
}

.modal-icon {
  font-size: 32px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  border-radius: 12px;
  flex-shrink: 0;
}

.modal-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 4px 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.modal-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background-color: transparent;
  color: var(--text-secondary);
  font-size: 20px;
  cursor: pointer;
  border-radius: var(--border-radius-md);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  background-color: var(--border-extra-light);
  color: var(--text-primary);
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-xl);
  background: #fff;
}

/* 用户信息区域 */
.user-info-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: rgba(64, 158, 255, 0.05);
  border-radius: 12px;
  margin-bottom: var(--spacing-lg);
  border: 1px solid rgba(64, 158, 255, 0.1);
}

.user-avatar-mini {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--primary-color);
  flex-shrink: 0;
}

.avatar-placeholder-mini {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
}

.user-info-text {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.user-school {
  font-size: 12px;
  color: var(--text-secondary);
}

.form-item {
  margin-bottom: var(--spacing-xl);
}

.content-item {
  margin-bottom: var(--spacing-lg);
}

.textarea-wrapper {
  position: relative;
}

.mention-list {
  position: absolute;
  bottom: 100%;
  left: 0;
  right: 0;
  max-height: 200px;
  overflow-y: auto;
  background: #fff;
  border: 2px solid var(--primary-color);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  margin-bottom: 8px;
}

.mention-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid var(--border-lighter);
}

.mention-item:last-child {
  border-bottom: none;
}

.mention-item:hover,
.mention-item.active {
  background: rgba(64, 158, 255, 0.1);
}

.mention-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
  overflow: hidden;
}

.mention-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.mention-info {
  flex: 1;
  min-width: 0;
}

.mention-name {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.mention-school {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.mention-empty {
  padding: var(--spacing-md);
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.label-icon {
  font-size: 18px;
}

.form-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 2px solid var(--border-base);
  border-radius: 16px;
  font-size: 15px;
  font-family: inherit;
  resize: vertical;
  outline: none;
  transition: all 0.3s;
  background: #fff;
  line-height: 1.6;
  min-height: 120px;
}

.form-textarea:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1);
  background: #fafbfc;
}

.char-counter {
  position: absolute;
  bottom: 12px;
  right: 16px;
  font-size: 12px;
  color: var(--text-secondary);
  background: rgba(255, 255, 255, 0.9);
  padding: 2px 8px;
  border-radius: 12px;
}

.char-counter .char-warning {
  color: #ff6b6b;
  font-weight: 600;
}

/* 话题输入区域 */
.topic-section {
  background: rgba(64, 158, 255, 0.02);
  padding: var(--spacing-md);
  border-radius: 16px;
  border: 1px solid rgba(64, 158, 255, 0.1);
}

.topic-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.topic-input-container {
  background: #fff;
  border: 2px solid var(--border-base);
  border-radius: 12px;
  padding: var(--spacing-sm);
  min-height: 48px;
  transition: all 0.3s;
}

.topic-input-container:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1);
}

.topic-tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  align-items: center;
}

.topic-tag-display {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  color: var(--primary-color);
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid rgba(64, 158, 255, 0.3);
}

.tag-remove-btn {
  width: 16px;
  height: 16px;
  border: none;
  background: rgba(64, 158, 255, 0.3);
  color: var(--primary-color);
  border-radius: 50%;
  font-size: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  transition: all 0.2s;
  flex-shrink: 0;
}

.tag-remove-btn:hover {
  background: var(--danger-color);
  color: #fff;
  transform: scale(1.1);
}

.topic-input {
  flex: 1;
  min-width: 120px;
  border: none;
  outline: none;
  font-size: 14px;
  padding: 6px 8px;
  background: transparent;
  color: var(--text-primary);
}

.topic-input::placeholder {
  color: var(--text-placeholder);
}

.preset-topics {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.preset-topics-label {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
}

.preset-topics-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.preset-topic-btn {
  padding: 6px 14px;
  border: 2px solid var(--border-base);
  border-radius: 18px;
  background: #fff;
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.preset-topic-btn:hover {
  border-color: var(--primary-color);
  background: rgba(64, 158, 255, 0.05);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.preset-topic-btn.active {
  border-color: var(--primary-color);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  color: var(--primary-color);
  font-weight: 600;
}

.image-upload-area {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.image-input {
  display: none;
}

/* 媒体上传区域 */
.media-section {
  background: rgba(64, 158, 255, 0.02);
  padding: var(--spacing-md);
  border-radius: 16px;
  border: 1px solid rgba(64, 158, 255, 0.1);
}

.media-upload-group {
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background: #fff;
  border-radius: 12px;
  border: 1px solid var(--border-lighter);
  transition: all 0.3s;
}

.media-upload-group:hover {
  border-color: rgba(64, 158, 255, 0.3);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.media-upload-group:last-child {
  margin-bottom: 0;
}

.upload-group-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-lighter);
}

.group-icon {
  font-size: 20px;
}

.group-title {
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
}

.group-badge {
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  color: #fff;
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 600;
}

.upload-btn {
  padding: 14px 24px;
  border: 2px dashed var(--border-base);
  border-radius: 12px;
  background: #fff;
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  width: 100%;
}

.upload-btn:hover {
  border-color: var(--primary-color);
  background: rgba(64, 158, 255, 0.05);
  color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.upload-btn-image {
  border-color: rgba(64, 158, 255, 0.3);
}

.upload-btn-video {
  border-color: rgba(255, 71, 87, 0.3);
}

.upload-btn-video:hover {
  border-color: #ff4757;
  background: rgba(255, 71, 87, 0.05);
  color: #ff4757;
}

.btn-icon {
  font-size: 18px;
}

.upload-hint {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: var(--spacing-xs);
  text-align: center;
}

.image-preview-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
}

.image-preview-item {
  position: relative;
  width: 100%;
  padding-top: 100%;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  border: 1px solid var(--border-lighter);
}

.preview-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  border: none;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  border-radius: 50%;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.remove-image-btn:hover {
  background-color: var(--danger-color);
}

.video-upload-area {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.video-input {
  display: none;
}

.video-preview {
  position: relative;
  width: 100%;
  max-width: 500px;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  border: 1px solid var(--border-lighter);
}

.preview-video {
  width: 100%;
  max-height: 300px;
  background-color: #000;
  display: block;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.remove-video-btn {
  margin-top: var(--spacing-md);
  padding: 10px 20px;
  border: 2px solid var(--danger-color);
  border-radius: 12px;
  background-color: transparent;
  color: var(--danger-color);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
}

.remove-video-btn:hover {
  background-color: var(--danger-color);
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3);
}

.friends-select-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.friend-option {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  border: 1px solid var(--border-lighter);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.2s;
}

.friend-option:hover {
  border-color: var(--primary-color);
  background-color: var(--border-extra-light);
}

.friend-option input {
  margin: 0;
}

.friend-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.friend-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--primary-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-weight: 600;
}

.friend-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.friend-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.friend-name {
  font-weight: 600;
  color: var(--text-primary);
}

.friend-school {
  font-size: 12px;
  color: var(--text-secondary);
}

.share-preview {
  margin-top: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--bg-color-page);
  border-radius: var(--border-radius-md);
  border: 1px dashed var(--border-base);
}

.share-preview-title {
  font-weight: 600;
  margin-bottom: var(--spacing-xs);
  color: var(--text-primary);
}

.share-preview-text {
  color: var(--text-secondary);
  line-height: 1.5;
  word-break: break-word;
}

.share-preview-thumb {
  margin-top: var(--spacing-sm);
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-lighter);
}

.venue-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid var(--border-base);
  border-radius: 12px;
  font-size: 14px;
  color: var(--text-primary);
  background-color: #fff;
  cursor: pointer;
  outline: none;
  transition: all 0.3s;
  font-family: inherit;
}

.venue-select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1);
}

/* 预览区域 */
.preview-section {
  background: rgba(0, 0, 0, 0.02);
  padding: var(--spacing-md);
  border-radius: 16px;
  border: 1px dashed var(--border-base);
}

.preview-content {
  margin-top: var(--spacing-sm);
}

.modal-footer {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  border-top: 2px solid rgba(64, 158, 255, 0.1);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.02) 0%, rgba(255, 255, 255, 0.02) 100%);
}

.footer-tips {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  font-size: 12px;
  color: var(--text-secondary);
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.footer-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
}

/* 用户信息弹窗样式 */
.user-info-modal {
  max-width: 480px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.user-info-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl) var(--spacing-lg) var(--spacing-lg);
}

.user-info-header {
  border-bottom: none;
  padding: var(--spacing-lg) var(--spacing-lg) var(--spacing-sm);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.12) 0%, rgba(118, 75, 162, 0.08) 100%);
}

.user-info-header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-info-header-sub {
  font-size: 13px;
  color: var(--text-secondary);
}

.user-info-header-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-info-body {
  padding-top: 0;
}

.user-info-avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.user-info-avatar-ring {
  position: relative;
  padding: 4px;
  border-radius: 999px;
  background: radial-gradient(circle at 0 0, #8e9bff, transparent 55%),
    radial-gradient(circle at 100% 0, #f9a8d4, transparent 55%),
    radial-gradient(circle at 50% 100%, #4facfe, transparent 55%);
}

.user-info-avatar-glow {
  position: absolute;
  inset: 4px;
  border-radius: inherit;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.65), transparent 60%);
  opacity: 0.9;
}

.user-info-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.85);
  flex-shrink: 0;
  position: relative;
  box-shadow: 0 14px 35px rgba(15, 23, 42, 0.35);
}

.avatar-placeholder-large {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 600;
}

.user-info-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info-badge-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xs);
}

.user-info-badge {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  color: var(--primary-color);
  background: rgba(64, 158, 255, 0.12);
  border: 1px solid rgba(64, 158, 255, 0.25);
}

.user-info-badge--success {
  color: #22c55e;
  background: rgba(34, 197, 94, 0.1);
  border-color: rgba(34, 197, 94, 0.3);
}

.user-info-details {
  text-align: center;
  width: 100%;
}

.user-info-name {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
}

.user-info-meta-row {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.user-info-username {
  font-size: 14px;
  color: var(--text-secondary);
}

.user-info-school {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xs);
}

.user-info-bio {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.6;
  margin: var(--spacing-md) 0;
  padding: var(--spacing-md);
  background: rgba(64, 158, 255, 0.05);
  border-radius: 8px;
}

.user-info-email {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: var(--spacing-xs);
}

.user-info-actions {
  display: flex;
  gap: var(--spacing-md);
  width: 100%;
  justify-content: center;
  margin-top: var(--spacing-md);
}

.user-info-actions .base-button {
  flex: 1;
  max-width: 150px;
}

/* 用户不存在状态样式 */
.user-not-found {
  padding: var(--spacing-xxl) var(--spacing-xl);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
}

.empty-icon-large {
  font-size: 64px;
  opacity: 0.6;
  margin-bottom: var(--spacing-sm);
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.empty-description {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.6;
  margin: 0;
}

.empty-description strong {
  color: var(--primary-color);
  font-weight: 600;
}

.empty-suggestions {
  margin-top: var(--spacing-md);
  padding: var(--spacing-lg);
  background: rgba(64, 158, 255, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(64, 158, 255, 0.1);
  text-align: left;
  width: 100%;
  max-width: 400px;
}

.suggestions-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.suggestions-list {
  margin: 0;
  padding-left: var(--spacing-lg);
  list-style: none;
}

.suggestions-list li {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.8;
  position: relative;
  padding-left: var(--spacing-md);
}

.suggestions-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: var(--primary-color);
  font-weight: bold;
}

.empty-close-btn {
  margin-top: var(--spacing-md);
  min-width: 120px;
}

.user-info-divider {
  width: 100%;
  height: 1px;
  margin-top: var(--spacing-lg);
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(0, 0, 0, 0.08) 15%,
    rgba(0, 0, 0, 0.08) 85%,
    transparent 100%
  );
}

.user-info-footer-hint {
  margin-top: var(--spacing-md);
  font-size: 12px;
  color: var(--text-secondary);
  text-align: center;
}

@media (max-width: 968px) {
  .social-layout {
    grid-template-columns: 1fr;
  }

  .social-sidebar {
    position: static;
  }

  .modal-content {
    max-width: 100%;
    max-height: 100vh;
    border-radius: 0;
  }
}
</style>

