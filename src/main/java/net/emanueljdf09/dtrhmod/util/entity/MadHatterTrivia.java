package net.emanueljdf09.dtrhmod.util.entity;

import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

    public static void startTrivia(PlayerEntity player, MadHatterEntity hatter) {
        UUID playerId = player.getUuid();
        ACTIVE_SESSIONS.put(playerId, new TriviaSession(hatter));

        player.sendMessage(Text.translatable("entity.dtrhmod.mad_hatter.trivia_start").formatted(Formatting.GOLD), false);
        sendNextQuestion(player);
    }

    public static void sendNextQuestion(PlayerEntity player) {
        UUID playerId = player.getUuid();
        TriviaSession session = ACTIVE_SESSIONS.get(playerId);

        if (session == null) return;

        if (session.getCurrentStep() >= 3) {
            player.sendMessage(Text.translatable("entity.dtrhmod.mad_hatter.trivia_pass").formatted(Formatting.GREEN), false);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                MadHatterEntity hatter = session.getHatter();
                if (hatter != null) {
                    hatter.rewardTriviaWin(serverPlayer);
                }
            }

            ACTIVE_SESSIONS.remove(playerId);
            return;
        }

        TriviaQuestion question;
        do {
            question = ALL_QUESTIONS[player.getWorld().random.nextInt(ALL_QUESTIONS.length)];
        } while (session.getUsedQuestions().contains(question));

        session.markQuestionUsed(question);
        session.setCurrentQuestion(question);

        player.sendMessage(Text.translatable("entity.dtrhmod.mad_hatter.riddle_header", (session.getCurrentStep() + 1), Text.translatable(question.getPromptKey())).formatted(Formatting.YELLOW), false);

        for (int i = 0; i < question.getOptions().length; i++) {
            Option opt = question.getOptions()[i];
            MutableText optionButton = Text.literal(" > ").append(Text.translatable(opt.getLangKey())).formatted(Formatting.AQUA);

            String commandPayload = "/madhatter_answer " + session.getHatter().getUuid() + " " + i;
            optionButton.setStyle(Style.EMPTY
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandPayload))
            );

            player.sendMessage(optionButton, false);
        }
    }

    public static void handleAnswerSelection(PlayerEntity player, MadHatterEntity hatter, int optionIndex) {
        UUID playerId = player.getUuid();
        TriviaSession session = ACTIVE_SESSIONS.get(playerId);

        if (session == null) {
            player.sendMessage(Text.translatable("entity.dtrhmod.mad_hatter.not_playing").formatted(Formatting.RED), false);
            return;
        }

        TriviaQuestion currentQuestion = session.getCurrentQuestion();
        Option selectedOption = currentQuestion.getOptions()[optionIndex];

        if (selectedOption.isCorrect()) {
            player.sendMessage(Text.translatable("entity.dtrhmod.mad_hatter.feedback_prefix").append(Text.translatable(currentQuestion.getSuccessDialogueKey())).formatted(Formatting.GREEN), false);
            session.incrementStep();
            sendNextQuestion(player);
        } else {
            player.sendMessage(Text.translatable("entity.dtrhmod.mad_hatter.feedback_prefix").append(Text.translatable(currentQuestion.getFailureDialogueKey())).formatted(Formatting.RED), false);
            hatter.registerTriviaFailure();
            ACTIVE_SESSIONS.remove(playerId);
        }
    }

    private static class TriviaSession {
        private final MadHatterEntity hatter;
        private int currentStep = 0;
        private TriviaQuestion currentQuestion;
        private final Set<TriviaQuestion> usedQuestions = new HashSet<>();

        public TriviaSession(MadHatterEntity hatter) {
            this.hatter = hatter;
        }

        public MadHatterEntity getHatter() { return hatter; }
        public int getCurrentStep() { return currentStep; }
        public void incrementStep() { this.currentStep++; }
        public TriviaQuestion getCurrentQuestion() { return currentQuestion; }
        public void setCurrentQuestion(TriviaQuestion question) { this.currentQuestion = question; }
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