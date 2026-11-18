package net.emanueljdf09.dtrhmod.util.entity;

import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.lang.ref.WeakReference;
import java.util.*;

public class MadHatterTrivia {
    private static final Map<UUID, TriviaSession> ACTIVE_SESSIONS = new HashMap<>();

    private static final TriviaQuestion[] ALL_QUESTIONS = {
            new TriviaQuestion(
                    "riddle.dtrhmod.raven",
                    "riddle.dtrhmod.raven.success",
                    "riddle.dtrhmod.raven.failure",
                    new Option("riddle.dtrhmod.raven.opt1", true),
                    new Option("riddle.dtrhmod.raven.opt2", false),
                    new Option("riddle.dtrhmod.raven.opt3", false)
            ),
            new TriviaQuestion(
                    "riddle.dtrhmod.tea",
                    "riddle.dtrhmod.tea.success",
                    "riddle.dtrhmod.tea.failure",
                    new Option("riddle.dtrhmod.tea.opt1", false),
                    new Option("riddle.dtrhmod.tea.opt2", true),
                    new Option("riddle.dtrhmod.tea.opt3", false)
            ),
            new TriviaQuestion(
                    "riddle.dtrhmod.hand",
                    "riddle.dtrhmod.hand.success",
                    "riddle.dtrhmod.hand.failure",
                    new Option("riddle.dtrhmod.hand.opt1", false),
                    new Option("riddle.dtrhmod.hand.opt2", false),
                    new Option("riddle.dtrhmod.hand.opt3", true)
            )

    };

   private static final int REQUIRED_CORRECT = Math.min(3, ALL_QUESTIONS.length);


    public static void clearSession(UUID playerId) {
        ACTIVE_SESSIONS.remove(playerId);
    }

    public static void clearAll() {
        ACTIVE_SESSIONS.clear();
    }

    public static void clearSessionsFor(MadHatterEntity hatter) {
        ACTIVE_SESSIONS.values().removeIf(session -> session.hatterId.equals(hatter.getUUID()));
    }


    public static void startTrivia(Player player, MadHatterEntity hatter) {
        UUID playerId = player.getUUID();
        ACTIVE_SESSIONS.put(playerId, new TriviaSession(hatter));

        player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.trivia_start").withStyle(ChatFormatting.GOLD), false);
        sendNextQuestion(player);
    }

    public static void sendNextQuestion(Player player) {
        UUID playerId = player.getUUID();
        TriviaSession session = ACTIVE_SESSIONS.get(playerId);

        if (session == null) return;

        MadHatterEntity hatter = session.getHatter();
        if (hatter == null) {
            ACTIVE_SESSIONS.remove(playerId);
            return;
        }

        if (session.getCurrentStep() >= REQUIRED_CORRECT) {
            player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.trivia_pass").withStyle(ChatFormatting.GREEN), false);

            if (player instanceof ServerPlayer serverPlayer) {
                hatter.rewardTriviaWin(serverPlayer);
            }

            ACTIVE_SESSIONS.remove(playerId);
            return;
        }

        TriviaQuestion question;
        do {
            question = ALL_QUESTIONS[player.level().random.nextInt(ALL_QUESTIONS.length)];
        } while (session.getUsedQuestions().contains(question));

        session.markQuestionUsed(question);
        session.setCurrentQuestion(question);

        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < question.getOptions().length; i++) order.add(i);
        Collections.shuffle(order, new Random(player.level().random.nextLong()));
        session.setCurrentOrder(order);

        player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.riddle_header", (session.getCurrentStep() + 1), Component.translatable(question.getPromptKey())).withStyle(ChatFormatting.YELLOW), false);

        for (int shown = 0; shown < order.size(); shown++) {
            Option opt = question.getOptions()[order.get(shown)];
            MutableComponent optionButton = Component.literal(" > ").append(Component.translatable(opt.getLangKey())).withStyle(ChatFormatting.AQUA);

            String commandPayload = "/madhatter_answer " + hatter.getUUID() + " " + shown;
            optionButton.setStyle(Style.EMPTY
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandPayload))
            );

            player.displayClientMessage(optionButton, false);
        }
    }

    public static void handleAnswerSelection(Player player, MadHatterEntity hatter, int optionIndex) {
        UUID playerId = player.getUUID();
        TriviaSession session = ACTIVE_SESSIONS.get(playerId);

        if (session == null) {
            player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.not_playing").withStyle(ChatFormatting.RED), false);
            return;
        }

        if (!session.hatterId.equals(hatter.getUUID()) || hatter.isBossMode()) {
            ACTIVE_SESSIONS.remove(playerId);
            player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.not_playing").withStyle(ChatFormatting.RED), false);
            return;
        }

        TriviaQuestion currentQuestion = session.getCurrentQuestion();
        List<Integer> order = session.getCurrentOrder();
        if (currentQuestion == null || order == null || optionIndex < 0 || optionIndex >= order.size()) {
            return;
        }

        Option selectedOption = currentQuestion.getOptions()[order.get(optionIndex)];

        if (selectedOption.isCorrect()) {
            player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.feedback_prefix").append(Component.translatable(currentQuestion.getSuccessDialogueKey())).withStyle(ChatFormatting.GREEN), false);
            session.incrementStep();
            sendNextQuestion(player);
        } else {
            player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.feedback_prefix").append(Component.translatable(currentQuestion.getFailureDialogueKey())).withStyle(ChatFormatting.RED), false);
            ACTIVE_SESSIONS.remove(playerId);
            hatter.registerTriviaFailure();
        }
    }

    private static class TriviaSession {
        private final WeakReference<MadHatterEntity> hatterRef;
        private final UUID hatterId;
        private int currentStep = 0;
        private TriviaQuestion currentQuestion;
        private List<Integer> currentOrder;
        private final Set<TriviaQuestion> usedQuestions = new HashSet<>();

        public TriviaSession(MadHatterEntity hatter) {
            this.hatterRef = new WeakReference<>(hatter);
            this.hatterId = hatter.getUUID();
        }

        public MadHatterEntity getHatter() {
            MadHatterEntity hatter = hatterRef.get();
            return hatter == null || hatter.isRemoved() ? null : hatter;
        }

        public int getCurrentStep() { return currentStep; }
        public void incrementStep() { this.currentStep++; }
        public TriviaQuestion getCurrentQuestion() { return currentQuestion; }
        public void setCurrentQuestion(TriviaQuestion question) { this.currentQuestion = question; }
        public List<Integer> getCurrentOrder() { return currentOrder; }
        public void setCurrentOrder(List<Integer> order) { this.currentOrder = order; }
        public Set<TriviaQuestion> getUsedQuestions() { return usedQuestions; }
        public void markQuestionUsed(TriviaQuestion q) { usedQuestions.add(q); }
    }

    public static class TriviaQuestion {
        private final String promptKey;
        private final String successDialogueKey;
        private final String failureDialogueKey;
        private final Option[] options;

        public TriviaQuestion(String promptKey, String successDialogueKey, String failureDialogueKey, Option... options) {
            this.promptKey = promptKey;
            this.successDialogueKey = successDialogueKey;
            this.failureDialogueKey = failureDialogueKey;
            this.options = options;
        }

        public String getPromptKey() { return promptKey; }
        public String getSuccessDialogueKey() { return successDialogueKey; }
        public String getFailureDialogueKey() { return failureDialogueKey; }
        public Option[] getOptions() { return options; }
    }

    public static class Option {
        private final String langKey;
        private final boolean correct;

        public Option(String langKey, boolean correct) {
            this.langKey = langKey;
            this.correct = correct;
        }

        public String getLangKey() { return langKey; }
        public boolean isCorrect() { return correct; }
    }
}