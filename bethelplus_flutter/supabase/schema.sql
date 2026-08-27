-- =========================================================
-- BÉTHEL+ CLOUD BACKEND SCHEMA (SUPABASE / POSTGRESQL)
-- =========================================================

-- 1. Table des Profils Utilisateurs
CREATE TABLE IF NOT EXISTS public.user_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    display_name TEXT,
    daily_goal_minutes INTEGER DEFAULT 15,
    streak_days INTEGER DEFAULT 0,
    theme_mode TEXT DEFAULT 'SYSTEM',
    dnd_enabled BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Active RLS sur user_profiles
ALTER TABLE public.user_profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Les utilisateurs peuvent lire leur propre profil"
ON public.user_profiles FOR SELECT
USING (auth.uid() = id);

CREATE POLICY "Les utilisateurs peuvent modifier leur propre profil"
ON public.user_profiles FOR INSERT WITH CHECK (auth.uid() = id);

CREATE POLICY "Les utilisateurs peuvent mettre à jour leur propre profil"
ON public.user_profiles FOR UPDATE
USING (auth.uid() = id);

-- 2. Table du Journal de Prière
CREATE TABLE IF NOT EXISTS public.prayer_journal (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL,
    is_answered BOOLEAN DEFAULT FALSE,
    answered_at TIMESTAMP WITH TIME ZONE,
    testimony TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.prayer_journal ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Les utilisateurs gèrent leurs propres prières"
ON public.prayer_journal FOR ALL
USING (auth.uid() = user_id);

-- 3. Table des Sessions de Prière & Méditation
CREATE TABLE IF NOT EXISTS public.prayer_sessions (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    duration_seconds INTEGER NOT NULL,
    date_millis BIGINT NOT NULL,
    meditation_id TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.prayer_sessions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Les utilisateurs gèrent leurs sessions de prière"
ON public.prayer_sessions FOR ALL
USING (auth.uid() = user_id);

-- 4. Table des Versets Mémorisés (Leitner)
CREATE TABLE IF NOT EXISTS public.memorized_verses (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    reference TEXT NOT NULL,
    text TEXT NOT NULL,
    box INTEGER DEFAULT 1,
    last_reviewed_at TIMESTAMP WITH TIME ZONE,
    next_review_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

ALTER TABLE public.memorized_verses ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Les utilisateurs gèrent leurs versets mémorisés"
ON public.memorized_verses FOR ALL
USING (auth.uid() = user_id);

-- 5. Index pour les performances
CREATE INDEX IF NOT EXISTS idx_prayer_journal_user ON public.prayer_journal(user_id);
CREATE INDEX IF NOT EXISTS idx_prayer_sessions_user ON public.prayer_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_memorized_verses_user ON public.memorized_verses(user_id);
