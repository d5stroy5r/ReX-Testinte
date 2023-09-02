package app.revanced.music.patches.utils;

import static app.revanced.music.patches.video.VideoInformation.getVideoId;
import static app.revanced.music.returnyoutubedislike.ReturnYouTubeDislike.Vote;
import static app.revanced.music.utils.ResourceUtils.identifier;

import android.graphics.Rect;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import app.revanced.music.patches.misc.VersionOverridePatch;
import app.revanced.music.returnyoutubedislike.ReturnYouTubeDislike;
import app.revanced.music.returnyoutubedislike.requests.ReturnYouTubeDislikeApi;
import app.revanced.music.settings.SettingsEnum;
import app.revanced.music.shared.PlayerType;
import app.revanced.music.utils.LogHelper;
import app.revanced.music.utils.ReVancedUtils;
import app.revanced.music.utils.ResourceType;

/**
 * Handles all interaction of UI patch components.
 * <p>
 * Does not handle creating dislike spans or anything to do with {@link ReturnYouTubeDislikeApi}.
 */
public class ReturnYouTubeDislikePatch {
    /**
     * Injection point.
     * <p>
     * Called when a spanned component is initially created,
     * and also when a Span is later reused again (such as scrolling off/on screen).
     * <p>
     * This method is sometimes called on the main thread, but it usually is called _off_ the main thread.
     * This method can be called multiple times for the same UI element (including after dislikes was added).
     *
     * @param textRef  Cache reference to the like/dislike char sequence,
     *                 which may or may not be the same as the original span parameter.
     *                 If dislikes are added, the atomic reference must be set to the replacement span.
     * @param original Original span that was created or reused by Litho.
     * @return The original span (if nothing should change), or a replacement span that contains dislikes.
     */
    @NonNull
    public static Spanned onComponentCreated(Spanned spanned) {
        return ReturnYouTubeDislike.onComponentCreated(spanned);
    }

    /**
     * Injection point.
     */
    public static void newVideoLoaded(@NonNull String videoId) {
        try {
            if (SettingsEnum.RYD_ENABLED.getBoolean() && !videoId.equals(currentVideoId)) {
                currentVideoId = videoId;
                ReturnYouTubeDislike.newVideoLoaded(videoId);
            }
        } catch (Exception ex) {
            LogHelper.printException(ReturnYouTubeDislikePatch.class, "newVideoLoaded failure", ex);
        }
    }

    /**
     * Injection point.
     * <p>
     * Called when the user likes or dislikes.
     *
     * @param vote int that matches {@link ReturnYouTubeDislike.Vote#value}
     */
    public static void sendVote(int vote) {
        try {
            if (!SettingsEnum.RYD_ENABLED.getBoolean()) {
                return;
            }
            for (Vote v : Vote.values()) {
                if (v.value == vote) {
                    ReturnYouTubeDislike.sendVote(v);
                    return;
                }
            }
            LogHelper.printException(ReturnYouTubeDislikePatch.class, "Unknown vote type: " + vote);
        } catch (Exception ex) {
            LogHelper.printException(ReturnYouTubeDislikePatch.class, "sendVote failure", ex);
        }
    }
}