import avatarA from '@/image/touxiang/Multiavatar-1712b989a2c4543c67.png';
import avatarB from '@/image/touxiang/Multiavatar-1aa830d7457b675f20.png';
import avatarC from '@/image/touxiang/Multiavatar-1df51d5d2ebabc4fe8.png';
import avatarD from '@/image/touxiang/Multiavatar-46e9cfc0049f9ce99a.png';
import avatarE from '@/image/touxiang/Multiavatar-4df0524f491f7e81a2.png';
import avatarF from '@/image/touxiang/Multiavatar-4f89fbdc56a7ee050d.png';
import avatarG from '@/image/touxiang/Multiavatar-97a8745543a0f094eb.png';
import avatarH from '@/image/touxiang/Multiavatar-Bugzilla.png';
import avatarI from '@/image/touxiang/Multiavatar-fbfae26517628e3237.png';
import avatarJ from '@/image/touxiang/Multiavatar-Vincent Plant.png';

const DEFAULT_AVATARS = {
  avatar_a: avatarA,
  avatar_b: avatarB,
  avatar_c: avatarC,
  avatar_d: avatarD,
  avatar_e: avatarE,
  avatar_f: avatarF,
  avatar_g: avatarG,
  avatar_h: avatarH,
  avatar_i: avatarI,
  avatar_j: avatarJ,
};

export function resolveAvatarUrl(avatar) {
  const value = String(avatar || '').trim();
  if (!value) return '';
  if (DEFAULT_AVATARS[value]) return DEFAULT_AVATARS[value];
  if (value.startsWith('/association/uploads/') || value.startsWith('/uploads/') || value.startsWith('http')) {
    return value;
  }
  return '';
}

export { DEFAULT_AVATARS };
